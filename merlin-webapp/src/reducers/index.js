import {combineReducers} from 'redux'
import config from './config';
import template from './template';

const reducers = combineReducers({
    // TODO ADD FILE UPLOAD REDUCERS (CACHE UPLOADS etc.)
    config,
    template
});

export default reducers;
