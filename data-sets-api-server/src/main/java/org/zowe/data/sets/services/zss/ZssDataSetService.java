/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2018, 2019
 */
package org.zowe.data.sets.services.zss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zowe.api.common.connectors.zss.ZssConnector;
import org.zowe.api.common.model.ItemsWrapper;
import org.zowe.data.sets.model.DataSet;
import org.zowe.data.sets.model.DataSetAttributes;
import org.zowe.data.sets.model.DataSetContentWithEtag;
import org.zowe.data.sets.model.DataSetCreateRequest;
import org.zowe.data.sets.services.DataSetService;

@Service("zssDataSetService")
public class ZssDataSetService implements DataSetService {

    @Autowired
    ZssConnector zssConnector;

    // TODO - review error handling, serviceability, https://github.com/zowe/data-sets/issues/16
    // use the zomsf error categories to work out errors
    // https://www.ibm.com/support/knowledgecenter/SSLTBW_2.3.0/com.ibm.zos.v2r3.izua700/IZUHPINFO_API_RESTFILES_Error_Categories.htm

    @Override
    public ItemsWrapper<String> listDataSetMembers(String dataSetName) {
        throw new UnsupportedOperationException("not supported using zss atm");
    }

    @Override
    public ItemsWrapper<DataSetAttributes> listDataSetAttributes(String filter) {
        ListDataSetsAttributesZssRequestRunner runner = new ListDataSetsAttributesZssRequestRunner(filter);
        return runner.run(zssConnector);
    }

    @Override
    public ItemsWrapper<DataSet> listDataSets(String filter) {
        throw new UnsupportedOperationException("not supported using zss atm");
    }

    @Override
    public DataSetContentWithEtag getContent(String dataSetName) {
        throw new UnsupportedOperationException("not supported using zss atm");
    }

    @Override
    public String putContent(String dataSetName, DataSetContentWithEtag contentWithEtag) {
        throw new UnsupportedOperationException("not supported using zss atm");
    }

    @Override
    public String createDataSet(DataSetCreateRequest request) {
        throw new UnsupportedOperationException("not supported using zss atm");
    }

    @Override
    public void deleteDataSet(String dataSetName) {
        throw new UnsupportedOperationException("not supported using zss atm");
    }
}