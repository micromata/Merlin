import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {getRestServiceUrl} from "../../../actions/global";

class RestUrlLink extends  React.Component {
    render() {
        const service = this.props.service;
        const params = this.props.params;
        var url;
        var displayUrl;
        if (params) {
            if (service === 'files/browse-local-filesystem') {
                url = getRestServiceUrl(service) + '?' + params;
            } else {
                url = getRestServiceUrl(service) + '?prettyPrinter=true&' + params;
            }
        } else {
            url = getRestServiceUrl(service) + '?prettyPrinter=true';
        }

        return (
            <a href={url}>rest/{service}{params ? '?' + params : ''}</a>
        )
    }
}

class RestServices extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            templateDefinitionId: '',
            templateDefinitionName: '',
            templateCanonicalPath: ''
        }
    }

    componentDidMount() {
        fetch(getRestServiceUrl("templates/list"), {
            method: "GET",
            dataType: "JSON",
            headers: {
                "Content-Type": "text/plain; charset=utf-8",
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                if (data.templateDefinitions[0]) {
                    this.setState({templateDefinitionId: data.templateDefinitions[0].id});
                    this.setState({templateDefinitionName: data.templateDefinitions[0].name});
                }
                if (data.templates[0]) {
                    this.setState({templateCanonicalPath: data.templates[0].fileDescriptor.canonicalPath});
                }
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }

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
                </ul>
                <h3>Templates</h3>
                <ul>
                    <li><RestUrlLink service='templates/list'/></li>
                    <li><RestUrlLink service='templates/definition-list'/></li>
                    <li><RestUrlLink service={'templates/definition/' + this.state.templateDefinitionId}/> (by id)</li>
                    <li><RestUrlLink service='templates/definition/Letter-Template'/> (by template name)</li>
                    <li><RestUrlLink service='templates/example'/> (example for run with json as post parameter: rest/templates/run)</li>
                </ul>
                <h4>How to proceed with a template:</h4>
                <ol>
                    <li>Get a list of all templates:<br/>
                        <RestUrlLink service='templates/list'/></li>
                    <li>Get a single template from list or get one by the canonical path via rest (path={this.state.templateCanonicalPath}):<br/>
                        <RestUrlLink service='templates/template' params={'canonicalPath=' +  encodeURIComponent(this.state.templateCanonicalPath)}/></li>
                    <li>You will receive a template including its template definition if assigned.</li>
                </ol>
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
