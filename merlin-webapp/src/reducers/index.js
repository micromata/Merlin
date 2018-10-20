import {combineReducers} from 'redux';
import log from './log';
import template from './template';
import version from './version';

const reducers = combineReducers({
    log,
    template,
    version
});

export default reducers;