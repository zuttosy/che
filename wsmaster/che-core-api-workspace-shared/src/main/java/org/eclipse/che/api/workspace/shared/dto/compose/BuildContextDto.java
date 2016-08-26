package org.eclipse.che.api.workspace.shared.dto.compose;

import org.eclipse.che.api.core.model.workspace.compose.BuildContext;
import org.eclipse.che.dto.shared.DTO;

/**
 * @author Alexander Garagatyi
 */
@DTO
public interface BuildContextDto extends BuildContext {
    void setContext(String context);

    BuildContextDto withContext(String context);

    void setDockerfile(String dockerfile);

    BuildContextDto withDockerfile(String dockerfile);
}
