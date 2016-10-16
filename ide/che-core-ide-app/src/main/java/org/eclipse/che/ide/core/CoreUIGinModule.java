/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.core;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Singleton;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.actions.find.FindActionView;
import org.eclipse.che.ide.actions.find.FindActionViewImpl;
import org.eclipse.che.ide.api.dialogs.ChoiceDialog;
import org.eclipse.che.ide.api.dialogs.ConfirmDialog;
import org.eclipse.che.ide.api.dialogs.DialogFactory;
import org.eclipse.che.ide.api.dialogs.InputDialog;
import org.eclipse.che.ide.api.dialogs.MessageDialog;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.WorkBenchView;
import org.eclipse.che.ide.api.preferences.PreferencePagePresenter;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriberFactory;
import org.eclipse.che.ide.api.project.wizard.ProjectNotificationSubscriber;
import org.eclipse.che.ide.api.theme.Theme;
import org.eclipse.che.ide.command.CommandProducerActionFactory;
import org.eclipse.che.ide.editor.synchronization.EditorContentSynchronizer;
import org.eclipse.che.ide.editor.synchronization.EditorContentSynchronizerImpl;
import org.eclipse.che.ide.editor.synchronization.EditorGroupSychronizationFactory;
import org.eclipse.che.ide.editor.synchronization.EditorGroupSynchronization;
import org.eclipse.che.ide.editor.synchronization.EditorGroupSynchronizationImpl;
import org.eclipse.che.ide.hotkeys.dialog.HotKeysDialogView;
import org.eclipse.che.ide.hotkeys.dialog.HotKeysDialogViewImpl;
import org.eclipse.che.ide.menu.MainMenuView;
import org.eclipse.che.ide.menu.MainMenuViewImpl;
import org.eclipse.che.ide.menu.StatusPanelGroupView;
import org.eclipse.che.ide.menu.StatusPanelGroupViewImpl;
import org.eclipse.che.ide.navigation.NavigateToFileView;
import org.eclipse.che.ide.navigation.NavigateToFileViewImpl;
import org.eclipse.che.ide.notification.NotificationManagerView;
import org.eclipse.che.ide.notification.NotificationManagerViewImpl;
import org.eclipse.che.ide.part.editor.EditorPartStackView;
import org.eclipse.che.ide.part.editor.recent.RecentFileActionFactory;
import org.eclipse.che.ide.part.editor.recent.RecentFileList;
import org.eclipse.che.ide.part.editor.recent.RecentFileStore;
import org.eclipse.che.ide.preferences.PreferencesView;
import org.eclipse.che.ide.preferences.PreferencesViewImpl;
import org.eclipse.che.ide.preferences.pages.appearance.AppearancePresenter;
import org.eclipse.che.ide.preferences.pages.appearance.AppearanceView;
import org.eclipse.che.ide.preferences.pages.appearance.AppearanceViewImpl;
import org.eclipse.che.ide.preferences.pages.extensions.ExtensionManagerPresenter;
import org.eclipse.che.ide.preferences.pages.extensions.ExtensionManagerView;
import org.eclipse.che.ide.preferences.pages.extensions.ExtensionManagerViewImpl;
import org.eclipse.che.ide.projectimport.wizard.ProjectNotificationSubscriberImpl;
import org.eclipse.che.ide.search.factory.FindResultNodeFactory;
import org.eclipse.che.ide.theme.DarkTheme;
import org.eclipse.che.ide.theme.LightTheme;
import org.eclipse.che.ide.ui.button.ConsoleButton;
import org.eclipse.che.ide.ui.button.ConsoleButtonFactory;
import org.eclipse.che.ide.ui.button.ConsoleButtonImpl;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogFooter;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogView;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogViewImpl;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogFooter;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogView;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogViewImpl;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogFooter;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogView;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogViewImpl;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogFooter;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogView;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogViewImpl;
import org.eclipse.che.ide.ui.dropdown.DropDownListFactory;
import org.eclipse.che.ide.ui.dropdown.DropDownWidget;
import org.eclipse.che.ide.ui.dropdown.DropDownWidgetImpl;
import org.eclipse.che.ide.ui.multisplitpanel.SubPanel;
import org.eclipse.che.ide.ui.multisplitpanel.SubPanelFactory;
import org.eclipse.che.ide.ui.multisplitpanel.panel.SubPanelPresenter;
import org.eclipse.che.ide.ui.multisplitpanel.panel.SubPanelView;
import org.eclipse.che.ide.ui.multisplitpanel.panel.SubPanelViewFactory;
import org.eclipse.che.ide.ui.multisplitpanel.panel.SubPanelViewImpl;
import org.eclipse.che.ide.ui.multisplitpanel.tab.Tab;
import org.eclipse.che.ide.ui.multisplitpanel.tab.TabItemFactory;
import org.eclipse.che.ide.ui.multisplitpanel.tab.TabWidget;
import org.eclipse.che.ide.ui.toolbar.MainToolbar;
import org.eclipse.che.ide.ui.toolbar.ToolbarPresenter;
import org.eclipse.che.ide.ui.toolbar.ToolbarView;
import org.eclipse.che.ide.ui.toolbar.ToolbarViewImpl;
import org.eclipse.che.ide.upload.file.UploadFileView;
import org.eclipse.che.ide.upload.file.UploadFileViewImpl;
import org.eclipse.che.ide.upload.folder.UploadFolderFromZipView;
import org.eclipse.che.ide.upload.folder.UploadFolderFromZipViewImpl;
import org.eclipse.che.ide.workspace.WorkspaceView;
import org.eclipse.che.ide.workspace.WorkspaceViewImpl;
import org.eclipse.che.ide.workspace.perspectives.general.PerspectiveViewImpl;

/**
 * //
 *
 * @author Artem Zatsarynnyi
 */
public class CoreUIGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        GinMultibinder<PreferencePagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencePagePresenter.class);
        prefBinder.addBinding().to(AppearancePresenter.class);
        prefBinder.addBinding().to(ExtensionManagerPresenter.class);

        GinMultibinder<Theme> themeBinder = GinMultibinder.newSetBinder(binder(), Theme.class);
        themeBinder.addBinding().to(DarkTheme.class);
        themeBinder.addBinding().to(LightTheme.class);

        // Resources
        bind(PartStackUIResources.class).to(Resources.class).in(Singleton.class);
        // Views
        bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
        bind(WorkBenchView.class).to(PerspectiveViewImpl.class).in(Singleton.class);
        bind(MainMenuView.class).to(MainMenuViewImpl.class).in(Singleton.class);
        bind(StatusPanelGroupView.class).to(StatusPanelGroupViewImpl.class).in(Singleton.class);

        bind(ToolbarView.class).to(ToolbarViewImpl.class);
        bind(ToolbarPresenter.class).annotatedWith(MainToolbar.class).to(ToolbarPresenter.class).in(Singleton.class);

        //configure drop down menu
        install(new GinFactoryModuleBuilder().implement(DropDownWidget.class, DropDownWidgetImpl.class)
                                             .build(DropDownListFactory.class));

        bind(NotificationManagerView.class).to(NotificationManagerViewImpl.class).in(Singleton.class);

        bind(EditorPartStackView.class);

        bind(EditorContentSynchronizer.class).to(EditorContentSynchronizerImpl.class).in(Singleton.class);
        install(new GinFactoryModuleBuilder().implement(EditorGroupSynchronization.class, EditorGroupSynchronizationImpl.class)
                                             .build(EditorGroupSychronizationFactory.class));

        bind(MessageDialogFooter.class);
        bind(MessageDialogView.class).to(MessageDialogViewImpl.class);
        bind(ConfirmDialogFooter.class);
        bind(ConfirmDialogView.class).to(ConfirmDialogViewImpl.class);
        bind(ChoiceDialogFooter.class);
        bind(ChoiceDialogView.class).to(ChoiceDialogViewImpl.class);
        bind(InputDialogFooter.class);
        bind(InputDialogView.class).to(InputDialogViewImpl.class);
        install(new GinFactoryModuleBuilder().implement(MessageDialog.class, MessageDialogPresenter.class)
                                             .implement(ConfirmDialog.class, ConfirmDialogPresenter.class)
                                             .implement(InputDialog.class, InputDialogPresenter.class)
                                             .implement(ChoiceDialog.class, ChoiceDialogPresenter.class)
                                             .build(DialogFactory.class));

        install(new GinFactoryModuleBuilder().implement(SubPanelView.class, SubPanelViewImpl.class)
                                             .build(SubPanelViewFactory.class));
        install(new GinFactoryModuleBuilder().implement(SubPanel.class, SubPanelPresenter.class)
                                             .build(SubPanelFactory.class));
        install(new GinFactoryModuleBuilder().implement(Tab.class, TabWidget.class)
                                             .build(TabItemFactory.class));

        install(new GinFactoryModuleBuilder().implement(ConsoleButton.class, ConsoleButtonImpl.class)
                                             .build(ConsoleButtonFactory.class));
        install(new GinFactoryModuleBuilder().implement(ProjectNotificationSubscriber.class,
                                                        ProjectNotificationSubscriberImpl.class)
                                             .build(ImportProjectNotificationSubscriberFactory.class));

        install(new GinFactoryModuleBuilder().build(FindResultNodeFactory.class));

        bind(UploadFileView.class).to(UploadFileViewImpl.class);
        bind(UploadFolderFromZipView.class).to(UploadFolderFromZipViewImpl.class);
        bind(PreferencesView.class).to(PreferencesViewImpl.class).in(Singleton.class);
        bind(NavigateToFileView.class).to(NavigateToFileViewImpl.class).in(Singleton.class);

        bind(ExtensionManagerView.class).to(ExtensionManagerViewImpl.class).in(Singleton.class);
        bind(AppearanceView.class).to(AppearanceViewImpl.class).in(Singleton.class);
        bind(FindActionView.class).to(FindActionViewImpl.class).in(Singleton.class);

        bind(HotKeysDialogView.class).to(HotKeysDialogViewImpl.class).in(Singleton.class);

        bind(RecentFileList.class).to(RecentFileStore.class).in(Singleton.class);

        install(new GinFactoryModuleBuilder().build(RecentFileActionFactory.class));

        install(new GinFactoryModuleBuilder().build(CommandProducerActionFactory.class));
    }
}
