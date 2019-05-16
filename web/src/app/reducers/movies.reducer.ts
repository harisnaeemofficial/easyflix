import {MoviesActionsUnion, MoviesActionTypes} from '../actions/movies.actions';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Movie} from '@app/models';

/**
 * State
 */
export const adapter: EntityAdapter<Movie> = createEntityAdapter<Movie>({
  selectId: movie => movie.id,
  sortComparer: (a, b) => a.title.localeCompare(b.title)
});

export interface State extends EntityState<Movie> {}

export const initialState: State = adapter.getInitialState({});

/**
 * Reducer
 */
export function reducer(
  state: State = initialState,
  action: MoviesActionsUnion
): State {
  switch (action.type) {

    case MoviesActionTypes.LoadMoviesSuccess: {
      return adapter.upsertMany(action.payload, state);
    }

    case MoviesActionTypes.AddMovies: {
      return adapter.upsertMany(action.payload, state);
    }

    case MoviesActionTypes.UpdateMovies: {
      return adapter.updateMany(
        action.payload.map(details => ({ id: details.id, changes: { details } })),
        state
      );
    }

    default: return state;
  }
}

/**
 * Selectors
 */
// export const getLoaded = (state: State) => state.loaded;
