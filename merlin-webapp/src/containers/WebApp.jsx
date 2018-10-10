import React from 'react';
import createBrowserHistory from 'history/createBrowserHistory';
import {Route, Router, Switch} from 'react-router';

import Menu from '../components/general/Menu';
import Start from '../components/views/Start';
import TemplateListView from '../components/views/templates/TemplateListView';
import Config from '../components/views/config/Configuration';
import TableExample from '../components/views/TableExample';
import FileUploadView from '../components/views/FileUpload';
import LogViewer from '../components/views/logging/LogViewer';
import RestServices from '../components/views/develop/RestServices';

class WebApp extends React.Component {

    render() {
        const history = createBrowserHistory();
        const routes = [
            ['Start', '/', Start],
            ['Templates', '/templates', TemplateListView],
            ['File Upload', '/drop', FileUploadView],
            ['Table Example', '/table', TableExample],
            ['Log viewer', '/logViewer', LogViewer],
            ['Rest services', '/restServices', RestServices],
            ['Configuration', '/config', Config]
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
