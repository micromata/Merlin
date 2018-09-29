import React from 'react';
import createBrowserHistory from 'history/createBrowserHistory';
import {Route, Router, Switch} from 'react-router';

import Menu from '../components/general/Menu';
import Start from '../components/views/Start';
import Config from '../components/views/config/ConfigView';
import TableExample from '../components/views/TableExample';
import FileUploadView from '../components/views/FileUpload';
import RestServices from '../components/views/develop/RestServices';
import TemplateListView from '../components/views/templates/TemplateListView';

class WebApp extends React.Component {

    render() {
        const history = createBrowserHistory();
        const routes = [
            ['Start', '/', Start],
            ['Config', '/config', Config],
            ['Table Example', '/table', TableExample],
            ['File Upload', '/drop', FileUploadView],
            ['Rest services', '/restServices', RestServices],
            ['Templates', '/templates', TemplateListView]
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
