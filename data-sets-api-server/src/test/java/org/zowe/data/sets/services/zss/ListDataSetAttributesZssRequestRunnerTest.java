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

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.Test;
import org.zowe.api.common.model.ItemsWrapper;
import org.zowe.data.sets.model.DataSetAttributes;
import org.zowe.data.sets.model.DataSetOrganisationType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

//TODO NOW - refactor with zosmf version

public class ListDataSetAttributesZssRequestRunnerTest extends AbstractZssRequestRunnerTest {

    @Test
    public void get_data_set_attributes_should_call_zosmf_and_parse_response_correctly() throws Exception {

        // ZSS doesn't return
//        DataSetAttributes stevenh = DataSetAttributes.builder().catalogName("ICFCAT.MV3B.MCAT").name("STEVENH")
//            .migrated(false).volumeSerial("3BSS01").build();

        // TODO SEAN - no idea why not returned
//        DataSetAttributes cobol = DataSetAttributes.builder().blockSize(32718).catalogName("ICFCAT.MV3B.CATALOGA")
//            .creationDate("2019/01/09").deviceType("3390").name("STEVENH.DEMO.COBOL").migrated(false)
//            .dataSetOrganization(DataSetOrganisationType.PO_E).expirationDate("***None***").recordLength(133)
//            .allocationUnit(AllocationUnitType.BLOCK).recordFormat("FBA").allocatedSize(201).used(0)
//            .volumeSerial("3BP001").build();

        // TODO SEAN - no idea why not returned
//        DataSetAttributes jcl = DataSetAttributes.builder().blockSize(6160).catalogName("ICFCAT.MV3B.CATALOGA")
//            .creationDate("2018/12/18").deviceType("3390").name("STEVENH.DEMO.JCL").migrated(false)
//            .dataSetOrganization(DataSetOrganisationType.PO).expirationDate("***None***").recordLength(80)
//            .allocationUnit(AllocationUnitType.CYLINDER).recordFormat("FB").allocatedSize(15).used(6)
//            .volumeSerial("3BP001").build();

        // TODO - default migrated to false?
        DataSetAttributes jcl = DataSetAttributes.builder().blockSize(32720).name("STEVENH.JCL")
            .dataSetOrganization(DataSetOrganisationType.PO_E).recordLength(80).recordFormat("FB")
            .volumeSerial("3BP001").build();

        DataSetAttributes sds = DataSetAttributes.builder().blockSize(27920).name("STEVENH.TEST")
            .dataSetOrganization(DataSetOrganisationType.PS).recordLength(80).recordFormat("FB").volumeSerial("3BP002")
            .build();

        // TODO LATER - can't get a migrated dataset on 3b
//        DataSetAttributes migrated = DataSetAttributes.builder().name("STEVENH.DEMO.MIGRATED").migrated(true).build();

        // TODO SEAN - no idea why not returned
//        DataSetAttributes sds = DataSetAttributes.builder().blockSize(1500).catalogName("ICFCAT.MV3B.CATALOGA")
//            .creationDate("2018/07/25").deviceType("3390").name("STEVENH.USER.LOG").migrated(false)
//            .dataSetOrganization(DataSetOrganisationType.PS).expirationDate("***None***").recordLength(150)
//            .allocationUnit(AllocationUnitType.TRACK).recordFormat("FB").allocatedSize(1).used(100)
//            .volumeSerial("3BP001").build();

        // TODO NOW - Got an error from ZSS - Type 1 or 8 DSCB for dataset STEVENH.VSAM not found?
        DataSetAttributes vsam = DataSetAttributes.builder().name("STEVENH.VSAM")
            // .dataSetOrganization(DataSetOrganisationType.VSAM)
            .build();

//        DataSetAttributes vsamData = DataSetAttributes.builder().catalogName("ICFCAT.MV3B.CATALOGA")
//            .creationDate("2019/01/09").deviceType("3390").name("STEVENH.VSAM.DATA")
//            .dataSetOrganization(DataSetOrganisationType.VSAM).expirationDate("***None***").migrated(false)
//            .allocatedSize(45).allocationUnit(AllocationUnitType.CYLINDER).volumeSerial("3BP001").build();
//
//        DataSetAttributes vsamIndex = DataSetAttributes.builder().catalogName("ICFCAT.MV3B.CATALOGA")
//            .creationDate("2019/01/09").deviceType("3390").name("STEVENH.VSAM.INDEX").migrated(false)
//            .dataSetOrganization(DataSetOrganisationType.VSAM).expirationDate("***None***")
//            .allocationUnit(AllocationUnitType.TRACK).allocatedSize(1).volumeSerial("3BP001").build();

        List<DataSetAttributes> dataSets = Arrays.asList(jcl, sds, vsam);
        ItemsWrapper<DataSetAttributes> expected = new ItemsWrapper<DataSetAttributes>(dataSets);
        String filter = "STEVENH*";

        mockJsonResponse(HttpStatus.SC_OK, loadTestFile("dataSetMetadata_detail.json"));
        RequestBuilder requestBuilder = mockGetBuilder(String.format("datasetMetadata/name/%s?detail=true", filter));
        when(zssConnector.request(requestBuilder)).thenReturn(response);

        assertEquals(expected, new ListDataSetsAttributesZssRequestRunner(filter).run(zssConnector));

        verifyInteractions(requestBuilder, true);
    }

    @Test
    public void get_data_set_attributes_no_results_should_call_zosmf_and_parse_response_correctly() throws Exception {
        ItemsWrapper<DataSetAttributes> expected = new ItemsWrapper<DataSetAttributes>(Collections.emptyList());
        String filter = "STEVENH*";

        mockJsonResponse(HttpStatus.SC_OK, loadTestFile("dataSetMetadata_noResults.json"));
        RequestBuilder requestBuilder = mockGetBuilder(String.format("datasetMetadata/name/%s?detail=true", filter));
        when(zssConnector.request(requestBuilder)).thenReturn(response);

        assertEquals(expected, new ListDataSetsAttributesZssRequestRunner(filter).run(zssConnector));

        verifyInteractions(requestBuilder, true);
    }

    // TODO - error tests get datasets once we can work out what they are
}
