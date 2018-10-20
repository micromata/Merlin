import React from 'react';
import createBrowserHistory from 'history/createBrowserHistory';
import {Route, Router, Switch} from 'react-router';

import Menu from '../components/general/Menu';
import Start from '../components/views/Start';
import TemplateListView from '../components/views/templates/TemplateListView';
import TemplateDefinitionListView from '../components/views/templates/TemplateDefinitionListView';
import ConfigurationPage from '../components/views/config/ConfigurationPage';
import FileUploadView from '../components/views/FileUpload';
import RestServices from '../components/views/develop/RestServices';
import {isDevelopmentMode} from '../utilities/global';
import TemplateView from '../components/views/templates/TemplateView';
import LogView from '../components/views/logging/LogView';
import TemplateDefinition from '../components/views/templates/templatedefinition/TemplateDefinition';
import '../css/my-style.css';
import Footer from '../components/views/footer/Footer';

class WebApp extends React.Component {

    render() {
        const history = createBrowserHistory();
        let routes = [
            ['Start', '/', Start],
            ['Templates', '/templates', TemplateListView],
            ['Definitions', '/templateDefinitions', TemplateDefinitionListView],
            ['Log viewer', '/logViewer', LogView],
            ['Configuration', '/config', ConfigurationPage]
        ];
        if (isDevelopmentMode()) {
            routes.push(['File Upload', '/drop', FileUploadView]);
            routes.push(['Rest services', '/restServices', RestServices]);
        }

        return (
            <Router history={history}>
                <div>
                    <Menu routes={routes}/>
                    <div className={'container main-view'}>
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
                            <Route path={'/templates/:primaryKey'} component={TemplateView}/>
                            <Route path={'/templateDefinitions/:primaryKey'} component={TemplateDefinition}/>
                        </Switch>
                    </div>
                    <Footer/>
                </div>
            </Router>
        );
    }
}

export default WebApp;
