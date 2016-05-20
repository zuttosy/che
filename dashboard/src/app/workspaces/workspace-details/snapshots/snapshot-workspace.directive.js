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

/**
 * @ngdoc directive
 * @name workspace.snapshot.directive:SnapshotWorkspaceDirective
 * @restrict E
 * @element
 *
 * @description
 * <snapshot-workspace workspace="workspace"></snapshot-workspace>
 *
 * @usage
 *   <snapshot-workspace workspace="workspace"></snapshot-workspace>
 *
 * @author Ann Shumilova
 */
export class SnapshotWorkspace {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor () {
    this.restrict = 'E';
    this.templateUrl = 'app/workspaces/workspace-details/snapshots/snapshot-workspace.html';

    this.controller = 'SnapshotWorkspaceController';
    this.controllerAs = 'snapshotWorkspaceCtrl';
    this.bindToController = true;

    this.scope = {
      workspaceId: '@workspaceId'
    };
  }

}
