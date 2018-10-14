import {combineReducers} from 'redux';
import log from './log';
import template from './template';

const reducers = combineReducers({
    log,
    template
});

export default reducers;