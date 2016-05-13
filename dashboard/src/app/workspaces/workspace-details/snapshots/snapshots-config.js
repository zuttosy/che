/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

import {SnapshotWorkspaceController} from './snapshot-workspace.controller';
import {SnapshotWorkspace} from './snapshot-workspace.directive';
/**
 * @ngdoc controller
 * @name snapshots:SnapshotsConfig
 * @description This class is used for configuring all workspace snapshots stuff.
 * @author Ann Shumilova
 */
export class SnapshotsConfig {

  constructor(register) {
    register.controller('SnapshotWorkspaceController', SnapshotWorkspaceController);
    register.directive('snapshotWorkspace', SnapshotWorkspace);
  }
}
