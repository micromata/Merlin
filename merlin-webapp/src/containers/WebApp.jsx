import React from 'react';
import createBrowserHistory from 'history/createBrowserHistory';
import {Route, Router, Switch} from 'react-router';

import Menu from '../components/general/Menu';
import Start from '../components/views/Start';
import TemplateListView from '../components/views/templates/TemplateListView';
import Config from '../components/views/config/Configuration';
import FileUploadView from '../components/views/FileUpload';
import LogViewer from '../components/views/logging/LogViewer';
import RestServices from '../components/views/develop/RestServices';
import {isDevelopmentMode} from '../actions/global';

class WebApp extends React.Component {

    render() {
        const history = createBrowserHistory();
        let routes = [
            ['Start', '/', Start],
            ['Templates', '/templates', TemplateListView],
            ['Log viewer', '/logViewer', LogViewer],
            ['Configuration', '/config', Config]
        ];
        if (isDevelopmentMode()) {
            routes.push(['File Upload', '/drop', FileUploadView]);
            routes.push(['Rest services', '/restServices', RestServices]);
        }

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
