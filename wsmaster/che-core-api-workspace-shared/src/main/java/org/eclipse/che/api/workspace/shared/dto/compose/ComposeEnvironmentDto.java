package org.eclipse.che.api.workspace.shared.dto.compose;

import org.eclipse.che.api.core.model.workspace.compose.ComposeEnvironment;
import org.eclipse.che.dto.shared.DTO;

import java.util.Map;

/**
 * @author Alexander Garagatyi
 */
@DTO
public interface ComposeEnvironmentDto extends ComposeEnvironment {
    void setVersion(String version);

    ComposeEnvironmentDto withVersion(String version);

    @Override
    Map<String, ComposeServiceDto> getServices();

    void setServices(Map<String, ComposeServiceDto> services);

    ComposeEnvironmentDto withServices(Map<String, ComposeServiceDto> services);
}
