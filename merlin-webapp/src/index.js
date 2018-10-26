import '../node_modules/bootstrap/dist/css/bootstrap.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {applyMiddleware, createStore} from 'redux';
import thunk from 'redux-thunk';
import {Provider} from 'react-redux';

import WebApp from './containers/WebApp';

import reducers from './reducers';

import './css/my-style.css';


let storedState = window.localStorage.getItem('state');

if (storedState) {
    storedState = JSON.parse(storedState);

    if (storedState.version) {
        storedState.version.loading = false;
    }

    if (storedState.log) {
        storedState.log.loading = false;
    }
}

const store = createStore(
    reducers,
    storedState || undefined,
    applyMiddleware(thunk)
);

store.subscribe(() => {
    window.localStorage.setItem('state', JSON.stringify(store.getState()));
});

ReactDOM.render(
    <Provider store={store}>
        <WebApp/>
    </Provider>,
    document.getElementById('root')
);
