import React from 'react';
import {PageHeader} from 'react-bootstrap';

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
                    <li><a href="http://localhost:8042/rest/configuration/config?prettyPrinter=true">rest/configuration/config</a></li>
                    <li><a href="http://localhost:8042/rest/configuration/config-ui?prettyPrinter=true">rest/configuration/config-ui</a></li>
                    <li><a href="http://localhost:8042/rest/configuration/config-old?prettyPrinter=true">rest/configuration/config-old</a></li>
                </ul>
                <h3>Templates</h3>
                <ul>
                    <li><a href="http://localhost:8042/rest/templates/list?prettyPrinter=true">rest/templates/list</a></li>
                </ul>
                <h3>Browse local filesystem</h3>
                <ul>
                    <li><a href="http://localhost:8042/rest/files/browse-local-filesystem?type=dir">rest/files/browse-local-filesystem?type=dir</a></li>
                    <li><a href="http://localhost:8042/rest/files/browse-local-filesystem?type=excel">rest/files/browse-local-filesystem?type=excel</a></li>
                    <li><a href="http://localhost:8042/rest/files/browse-local-filesystem?type=word">rest/files/browse-local-filesystem?type=word</a></li>
                    <li><a href="http://localhost:8042/rest/files/browse-local-filesystem?type=file">rest/files/browse-local-filesystem?type=file</a></li>
                </ul>
            </div>
        );
    }
}

export default RestServices;
