import React from 'react';
import ReactDOM from 'react-dom';
import {createStore} from 'redux';
import {Provider} from 'react-redux';

import WebApp from './containers/WebApp';

import reducer from './reducers';

import registerServiceWorker from './registerServiceWorker';

const store = createStore(reducer);

ReactDOM.render(
    <Provider store={store}>
        <WebApp/>
    </Provider>,
    document.getElementById('root')
);

registerServiceWorker();
