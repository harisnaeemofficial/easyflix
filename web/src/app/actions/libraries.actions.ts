import {Action} from '@ngrx/store';
import {Library} from '@app/models/file';

export enum LibrariesActionTypes {
  LoadLibraries = 'libraries/load',
  LoadLibrariesSuccess = 'libraries/load/success',
  LoadLibrariesError = 'libraries/load/error',
  AddLibrary = 'libraries/add',
  AddLibrarySuccess = 'libraries/add/success',
  AddLibraryError = 'libraries/add/error',
  RemoveLibrary = 'libraries/remove',
  RemoveLibrarySuccess = 'libraries/remove/success',
  RemoveLibraryError = 'libraries/remove/error',
}

export class LoadLibraries implements Action {
  readonly type = LibrariesActionTypes.LoadLibraries;
}

export class LoadLibrariesSuccess implements Action {
  readonly type = LibrariesActionTypes.LoadLibrariesSuccess;
  constructor(public payload: Library[]) {}
}

export class LoadLibrariesError implements Action {
  readonly type = LibrariesActionTypes.LoadLibrariesError;
  constructor(public payload: string) {}
}

export class AddLibrary implements Action {
  readonly type = LibrariesActionTypes.AddLibrary;
  constructor(public payload: Library) {}
}

export class AddLibrarySuccess implements Action {
  readonly type = LibrariesActionTypes.AddLibrarySuccess;
  constructor(public payload: Library) {}
}

export class AddLibraryError implements Action {
  readonly type = LibrariesActionTypes.AddLibraryError;
  constructor(public payload: string) {}
}

export class RemoveLibrary implements Action {
  readonly type = LibrariesActionTypes.RemoveLibrary;
  constructor(public payload: string) {}
}

export class RemoveLibrarySuccess implements Action {
  readonly type = LibrariesActionTypes.RemoveLibrarySuccess;
  constructor(public payload: string) {}
}

export class RemoveLibraryError implements Action {
  readonly type = LibrariesActionTypes.RemoveLibraryError;
  constructor(public payload: string) {}
}

export type LibrariesActionsUnion =
  LoadLibraries |
  LoadLibrariesSuccess |
  LoadLibrariesError |
  AddLibrary |
  AddLibrarySuccess |
  AddLibraryError |
  RemoveLibrary |
  RemoveLibrarySuccess |
  RemoveLibraryError;