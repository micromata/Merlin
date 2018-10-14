import React from 'react';
import ReactDOM from 'react-dom';
import {applyMiddleware, createStore} from 'redux';
import thunk from 'redux-thunk';
import {Provider} from 'react-redux';

import WebApp from './containers/WebApp';

import registerServiceWorker from './registerServiceWorker';
import reducers from './reducers';

let storedState = window.localStorage.getItem('state');

const store = createStore(
    reducers,
    storedState ? JSON.parse(storedState) : undefined,
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

registerServiceWorker();
