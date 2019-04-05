/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */
package org.zowe.data.sets.services.zss;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import org.zowe.api.common.connectors.ZConnector;
import org.zowe.api.common.model.ItemsWrapper;
import org.zowe.api.common.services.AbstractZRequestRunner;
import org.zowe.api.common.utils.ResponseCache;
import org.zowe.data.sets.model.DataSetAttributes;
import org.zowe.data.sets.model.DataSetOrganisationType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ListDataSetsAttributesZssRequestRunner extends AbstractZRequestRunner<ItemsWrapper<DataSetAttributes>> {

    protected String filter;

    public ListDataSetsAttributesZssRequestRunner(String filter) {
        this.filter = filter;
    }

    @Override
    protected RequestBuilder prepareQuery(ZConnector zosmfConnector) throws URISyntaxException, IOException {
        String path = String.format("datasetMetadata/name/%s", filter);
        URI requestUrl = zosmfConnector.getFullUrl(path, "detail=true"); // $NON-NLS-1$
        RequestBuilder requestBuilder = RequestBuilder.get(requestUrl);
        return requestBuilder;
    }

    @Override
    protected int[] getSuccessStatus() {
        return new int[] { HttpStatus.SC_OK };
    }

    @Override
    protected ItemsWrapper<DataSetAttributes> getResult(ResponseCache responseCache) throws IOException {
        JsonObject response = responseCache.getEntityAsJsonObject();
        JsonElement items = response.get("datasets");
        List<DataSetAttributes> dataSets = new ArrayList<>();
        for (JsonElement jsonElement : items.getAsJsonArray()) {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                DataSetAttributes zssDataSet = DataSetAttributes.builder().name(name(jsonObject))
                    .volumeSerial(volumeSerial(jsonObject)).blockSize(blockSize(jsonObject))
                    .dataSetOrganization(dataSetOrganization(jsonObject)).recordLength(recordLength(jsonObject))
                    .recordFormat(recordFormat(jsonObject)).build();

                dataSets.add(
                        zssDataSet/* DataSetMapper.INSTANCE.zssToDataSetAttributesDTO(jsonElement.getAsJsonObject()) */);
            } catch (IllegalArgumentException e) {
                log.error("listDataSetAttributes", e);
            }
        }
        return new ItemsWrapper<>(dataSets);
    }

    // TODO - speak to Andrei
    public String name(JsonObject in) {
        String name = getStringOrNull(in, "name");
        // TODO speak to Sean about trailing whitespace?
        if (name != null) {
            name = name.trim();
        }
        return name;
    }

    public Integer blockSize(JsonObject in) {
        JsonObject dsorgJsonObject = getJsonObjectOrNull(in, "dsorg");
        if (dsorgJsonObject != null) {
            return getIntegerOrNull(dsorgJsonObject, "totalBlockSize");

        }
        return null;
    }

    public String volumeSerial(JsonObject in) {
        return getStringOrNull(in, "volser");
    }

    // TODO - consider creating model object for dataSetOrganization, with type, dir and extended??
    public DataSetOrganisationType dataSetOrganization(JsonObject in) {
        JsonObject dsorgJsonObject = getJsonObjectOrNull(in, "dsorg");
        if (dsorgJsonObject != null) {
            String organization = getStringOrNull(dsorgJsonObject, "organization");
            Boolean isExtended = getBooleanOrNull(dsorgJsonObject, "isPDSE");
            return DataSetOrganisationType.getByZss(organization, isExtended);
        }
        return null;
    }

    public Integer recordLength(JsonObject in) {
        JsonObject dsorgJsonObject = getJsonObjectOrNull(in, "dsorg");
        if (dsorgJsonObject != null) {
            return getIntegerOrNull(dsorgJsonObject, "maxRecordLen");

        }
        return null;
    }

    // TODO - investigate carriageControl - consider creating model object for recfm?
    public String recordFormat(JsonObject in) {
        JsonObject recfmJsonObject = getJsonObjectOrNull(in, "recfm");
        if (recfmJsonObject != null) {
            StringBuilder recfm = new StringBuilder(getStringOrNull(recfmJsonObject, "recordLength"));
            Boolean isBlocked = getBooleanOrNull(recfmJsonObject, "isBlocked");
            if (isBlocked) {
                recfm.append("B");
            }
            return recfm.toString();
        }

        return null;
    }

    private JsonObject getJsonObjectOrNull(JsonObject json, String key) {
        JsonObject value = null;
        JsonElement jsonElement = json.get(key);
        if (!(jsonElement == null || jsonElement.isJsonNull()) && jsonElement.isJsonObject()) {
            value = jsonElement.getAsJsonObject();
        }
        return value;
    }

    private Boolean getBooleanOrNull(JsonObject json, String key) {
        Boolean value = null;
        JsonElement jsonElement = json.get(key);
        if (!(jsonElement == null || jsonElement.isJsonNull())) {
            value = jsonElement.getAsBoolean();
        }
        return value;
    }
}
