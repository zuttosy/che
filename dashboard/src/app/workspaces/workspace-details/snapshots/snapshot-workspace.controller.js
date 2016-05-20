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

    this.isAutoSnapshot = this.getAutoSnapshotWorkspace();
    this.isAutoRestore = this.getAutoRestoreWorkspace();
  }

  doSnapshot() {
    this.snapshotInProgress = true;

    this.cheWorkspace.createSnapshot(this.workspaceId).then(() => {
      this.processSnapshotState();
    }, (error) => {
      this.handleError(error.data.message);
    });
  }

  processSnapshotState() {
    this.cheWorkspace.fetchStatusChange(this.workspaceId, 'SNAPSHOT_CREATION_ERROR').then((message) => {
      this.handleError(message.error);
    }, (error) => {
      this.handleError(error.data.message);
    });

    this.cheWorkspace.fetchStatusChange(this.workspaceId, 'SNAPSHOT_CREATED').then((message) => {
      this.snapshotInProgress = false;
      alert('created');
    }, (error) => {
      this.handleError(error.data.message);
    });
  }

  handleError(message) {
    let workspace = this.cheWorkspace.getWorkspaceById(this.workspaceId);
    let workspaceName = workspace.config.name;
    this.snapshotInProgress = false;
    this.notification.showError(message ? message : 'Failed to snapshot ' + workspaceName + ' workspace.');
  }

  /**
   * Returns current status of workspace
   * @returns {String}
   */
  getWorkspaceStatus() {
    let workspace = this.cheWorkspace.getWorkspaceById(this.workspaceId);
    return workspace ? workspace.status : null;
  }

  onAutoSnapshotChanged() {
    let workspace = this.cheWorkspace.getWorkspaceById(this.workspaceId);
    let workspaceData = angular.copy(workspace);
    workspaceData.attributes['auto_snapshot'] = this.isAutoSnapshot;
    this.updateWorkspace(workspaceData);
  }

  getAutoSnapshotWorkspace() {
    let workspace = this.cheWorkspace.getWorkspaceById(this.workspaceId);
    let autoSnapshot = workspace.attributes['auto_snapshot'];
    return autoSnapshot ? autoSnapshot === 'true' : false;
  }

  onAutoRestoreChanged() {
    let workspace = this.cheWorkspace.getWorkspaceById(this.workspaceId);
    let workspaceData = angular.copy(workspace);
    workspaceData.attributes['auto_restore'] = this.isAutoRestore;
    this.updateWorkspace(workspaceData);
  }

  getAutoRestoreWorkspace() {
    let workspace = this.cheWorkspace.getWorkspaceById(this.workspaceId);
    let autoRestore = workspace.attributes['auto_restore'];
    return autoRestore ? autoRestore === 'true' : false;
  }

  updateWorkspace(workspaceData) {
    let promise = this.cheWorkspace.updateWorkspace(this.workspaceId, workspaceData);
    promise.then((data) => {
    //  this.isAutoSnapshot = this.getAutoSnapshotWorkspace();
    //  this.isAutoRestore = this.getAutoRestoreWorkspace();
    }, (error) => {
      this.notification.showError(error.data.message !== null ? error.data.message : 'Failed to update workspace.');
    });
  }


}
