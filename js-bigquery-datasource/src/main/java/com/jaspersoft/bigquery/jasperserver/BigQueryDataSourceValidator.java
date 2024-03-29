/*
 * Copyright (C) 2005 - 2012 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.bigquery.jasperserver;

import java.util.Map;

import org.springframework.validation.Errors;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceValidator;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryDataSourceValidator implements CustomDataSourceValidator {
    @Override
    public void validatePropertyValues(CustomReportDataSource customReportDataSource, Errors errors) {
        Map<?, ?> propertyMap = customReportDataSource.getPropertyMap();
        String serviceAccountId = (String) propertyMap.get("serviceAccountId");
        if (serviceAccountId == null || serviceAccountId.length() == 0) {
            errors.rejectValue("reportDataSource.propertyMap[serviceAccountId]",
                    "BigQueryDataSource.serviceAccountId.required");
        }
        String privateKeyFilePath = (String) propertyMap.get("privateKeyFilePath");
        if (privateKeyFilePath == null || privateKeyFilePath.length() == 0) {
            errors.rejectValue("reportDataSource.propertyMap[privateKeyFilePath]",
                    "BigQueryDataSource.privateKeyFilePath.required");
        }
        String projectId = (String) propertyMap.get("projectId");
        if (projectId == null || projectId.length() == 0) {
            errors.rejectValue("reportDataSource.propertyMap[projectId]", "BigQueryDataSource.projectId.required");
        }
    }
}