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
 * @ngdoc controller
 * @name workspace.snapshot.controller:SnapshotWorkspaceController
 * @description This class is handling the controller for the snapshoting workspace
 * @author Ann Shumilova
 */
export class SnapshotWorkspaceController {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor(cheWorkspace, cheNotification) {
    this.cheWorkspace = cheWorkspace;
    this.notification = cheNotification;
    this.snapshotInProgress = false;
  }

  doSnapshot() {
    this.snapshotInProgress = true;
    let workspaceName = this.workspace.config.name;
    this.cheWorkspace.createSnapshot(this.workspace.id).then(() => {
    }, (error) => {
      this.snapshotInProgress = false;
      this.notification.showError(error.data.message ? error.data.message : 'Failed to snapshot ' + workspaceName + ' workspace.');
    });
  }

  processSnapshotState() {
    //this.cheWorkspace.fetchStatusChange(this.workspace.id, 'SNAPSHOT_CREATION_ERROR').then()
  }


  /**
   * Returns current status of workspace
   * @returns {String}
   */
  getWorkspaceStatus() {
    let workspace = this.cheWorkspace.getWorkspaceById(this.workspace.id);
    return workspace ? workspace.status : null;
  }
}
