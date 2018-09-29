import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {getRestServiceUrl} from "../../../actions/global";

class RestUrlLink extends  React.Component {
    render() {
        const service = this.props.service;
        const params = this.props.params;

        return (
            <a href={getRestServiceUrl(service) + (params ? '?' + params : '?prettyPrinter=true')}>rest/{service}{params ? '?' + params : ''}</a>
        )
    }
}

class RestServices extends React.Component {

    render() {
        return (
            <div>
                <PageHeader>
                    Rest Services
                </PageHeader>
                <h3>
                    Config
                </h3>
                <ul>
                    <li><RestUrlLink service='configuration/config'/></li>
                    <li><RestUrlLink service='configuration/config-ui'/></li>
                    <li><RestUrlLink service='configuration/config-old'/></li>
                </ul>
                <h3>Templates</h3>
                <ul>
                    <li><RestUrlLink service='templates/list'/></li>
                    <li><RestUrlLink service='templates/9MJdzFN2v2PKMJ9erj59'/> (by id)</li>
                    <li><RestUrlLink service='templates/Letter-Template'/> (by template name)</li>
                    <li><RestUrlLink service='templates/example'/> (example for run with json as post parameter: rest/templates/run)</li>
                </ul>
                <h3>Browse local filesystem</h3>
                <ul>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=dir'/></li>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=excel'/></li>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=word'/></li>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=file'/></li>
                </ul>
            </div>
        );
    }
}

export default RestServices;
