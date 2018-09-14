import React from 'react';
import ReactDOM from 'react-dom';
import {applyMiddleware, createStore} from 'redux';
import thunk from 'redux-thunk';
import {Provider} from 'react-redux';

import WebApp from './containers/WebApp';

import reducer from './reducers';

import registerServiceWorker from './registerServiceWorker';

const store = createStore(
    reducer,
    applyMiddleware(thunk)
);

ReactDOM.render(
    <Provider store={store}>
        <WebApp/>
    </Provider>,
    document.getElementById('root')
);

registerServiceWorker();
