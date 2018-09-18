import React from 'react';
import createBrowserHistory from 'history/createBrowserHistory';
import {Route, Router, Switch} from 'react-router';

import Menu from '../components/general/Menu';
import Start from '../components/views/Start';
import Config from '../components/views/config/View';
import DynamicConfig from '../components/views/DynamicConfig';
import TableExample from '../components/views/TableExample';
import FileUploadView from '../components/views/FileUpload';

class WebApp extends React.Component {

    render() {
        const history = createBrowserHistory();
        const routes = [
            ['Start', '/', Start],
            ['Config', '/config', Config],
            ['DynamicConfig', '/dynamicConfig', DynamicConfig],
            ['Table Example', '/table', TableExample],
            ['File Upload', '/drop', FileUploadView]
        ];

        return (
            <Router history={history}>
                <div>
                    <Menu routes={routes}/>
                    <div className={'container'}>
                        <Switch>
                            {
                                routes.map((route, index) => (
                                    <Route
                                        key={index}
                                        path={route[1]}
                                        component={route[2]}
                                        exact
                                    />
                                ))
                            }
                        </Switch>
                    </div>
                </div>
            </Router>
        );
    }
}

export default WebApp;
