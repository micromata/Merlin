import React from 'react';
import {PageHeader} from '../../general/BootstrapComponents';
import {getRestServiceUrl, getResponseHeaderFilename} from "../../../utilities/global";
import downloadFile from "../../../utilities/download";

class RestUrlLink extends React.Component {
    render() {
        const service = this.props.service;
        const params = this.props.params;
        var url;
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
            templatePrimaryKey: '',
        }
        this.onRun = this.onRun.bind(this);
    }

    componentDidMount() {
        fetch(getRestServiceUrl("templates/example-definitions"), {
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
                    this.setState({
                        templateDefinitionId: data.templateDefinitionId,
                        templatePrimaryKey: data.templatePrimaryKey
                    });
                }
            )
            .catch((error) => {
                    console.log(error, "Oups, what's happened?")
                }
            )
    }

    onRun() {
        let filename;
        fetch(getRestServiceUrl('templates/example-run-data'), {
            method: "GET",
            dataType: "JSON",
            headers: {
                "Content-Type": "text/plain; charset=utf-8"
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                fetch(getRestServiceUrl("templates/run"), {
                    method: 'POST',
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        filename = getResponseHeaderFilename(response.headers.get("Content-Disposition"));
                        //this.setState({downloadFilename: filename});
                        return response.blob();
                    })
                    .then(blob => downloadFile(blob, filename));
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }

    render() {
        return (
            <React.Fragment>
                <PageHeader>
                    Rest Services
                </PageHeader>
                <h3>Templates</h3>
                <ul>
                    <li><RestUrlLink service='templates/list'/></li>
                    <li><RestUrlLink service='templates/definition-list'/></li>
                    <li><RestUrlLink service={'templates/definition'} params={`id=${this.state.templateDefinitionId}`}/> (by id)</li>
                </ul>
                <h4>How to get and run a template:</h4>
                <ol>
                    <li>Get a list of all templates:<br/>
                        <RestUrlLink service='templates/list'/></li>
                    <li>Get a single template from list or get one by its primary key via rest
                        (primaryKey={this.state.templatePrimaryKey}):<br/>
                        <RestUrlLink service='templates/template'
                                     params={'primaryKey=' + encodeURIComponent(this.state.templatePrimaryKey)}/>
                    </li>
                    <li>You will receive a template including its template definition if assigned.</li>
                    <li>Run template with <a
                        href={getRestServiceUrl('templates/example-run-data') + '?prettyPrinter=true'}>json post parameter</a> for service<br/>
                        <button tabIndex={1} onClick={this.onRun} type="button" className="btn btn-link">rest/templates/run</button>
                    </li>
                </ol>
                <h3>
                    Config
                </h3>
                <ul>
                    <li><RestUrlLink service='configuration/config'/></li>
                    <li><RestUrlLink service='configuration/config-ui'/> (as a trial for dynamic forms)</li>
                    <li><RestUrlLink service='version'/> Gets the version and build date of the server.</li>
                    <li><RestUrlLink service='updates/info'/> Gets the update version.</li>
                    <li><RestUrlLink service='i18n/list'/> Gets all translations.</li>
                </ul>
                <h3>Browse local filesystem</h3>
                <ul>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=dir'/></li>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=excel'/></li>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=word'/></li>
                    <li><RestUrlLink service='files/browse-local-filesystem' params='type=file'/></li>
                </ul>
                <h3>Logging</h3>
                <ul>
                    <li><RestUrlLink service='logging/query'/> (all, default is info log level as treshold)</li>
                    <li><RestUrlLink service='logging/query' params={'treshold=warn'}/> (only warnings)</li>
                    <li><RestUrlLink service='logging/query' params={'treshold=info&search=server'}/> (search for server)</li>
                </ul>
            </React.Fragment>
        );
    }
}

export default RestServices;
