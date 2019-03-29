import {NgModule} from '@angular/core';
import {FolderComponent} from './library/folder/folder.component';
import {SettingsComponent} from './settings/settings.component';
import {ControlsComponent} from './video/controls/controls.component';
import {VideoComponent} from './video/video.component';
import {SearchComponent} from './search/search.component';
import {LibraryComponent} from './library/library.component';
import {HistoryComponent} from './history/history.component';
import {SharedModule} from '../shared/shared.module';
import {HomeComponent} from './home/home.component';
import {AboutComponent} from './about/about.component';
import {SidenavComponent} from './sidenav.component';
import {LibrariesViewComponent} from './library/libraries-view/libraries-view.component';

const COMPONENTS = [
  FolderComponent,
  SettingsComponent,
  ControlsComponent,
  VideoComponent,
  SearchComponent,
  AboutComponent,
  LibraryComponent,
  HistoryComponent,
  HomeComponent,
  SidenavComponent,
  LibrariesViewComponent
];

@NgModule({
  imports: [SharedModule],
  declarations: COMPONENTS,
  exports: COMPONENTS,
  entryComponents: [
    LibrariesViewComponent,
    FolderComponent
  ]
})
export class ComponentsModule {}