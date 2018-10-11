import React from 'react';
import {PageHeader, Table} from 'react-bootstrap';
import Highlight from 'react-highlighter';
import {FormLabel, FormButton, FormSelect, FormInput} from "../../general/forms/FormComponents";
import {getRestServiceUrl} from "../../../actions/global";
import './LogViewer.css';

class LogViewerData extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            search: '',
            treshold: 'info',
            maxSize: 100,
            locationFormat: 'none'
        }
    }

    handleTextChange = event => {
        event.preventDefault();
        if (event.target.name === 'treshold' ||
            event.target.name === 'maxSize') {
            this.setState({[event.target.name]: event.target.value}, () => this.reload());
        } else {
            this.setState({[event.target.name]: event.target.value});
        }
    }

    onSubmit = event => {
        event.preventDefault();
        this.reload();
    }

    reload = () => {
        this.setState({
            isFetching: true,
            failed: false,
            logEntries: undefined
        });
        fetch(getRestServiceUrl("logging/query", {
            search: this.state.search,
            treshold: this.state.treshold,
            maxSize: this.state.maxSize
        }), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                const logEntries = json.map(logEntry => {
                    return {
                        level: logEntry.level.toLowerCase(),
                        message: logEntry.message,
                        timestamp: logEntry.timestamp,
                        javaClass: logEntry.javaClass,
                        javaClassSimpleName: logEntry.javaClassSimpleName,
                        lineNumber: logEntry.lineNumber,
                        methodName: logEntry.methodName
                    };
                });
                this.setState({
                    isFetching: false,
                    logEntries
                })
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    }

    locationString = entry => {
        if (this.state.display === 'short') {
            return `${entry.javaClassSimpleName}:${entry.methodName}:${entry.lineNumber}`;
        } else if (this.state.display === 'normal') {
            return `${entry.javaClass}:${entry.methodName}:${entry.lineNumber}`;
        }
        return null;
    }

    render() {
        const rows = [];
        if (this.state.logEntries) {
            let counter = 0;
            let searchLower = this.state.search.toLowerCase();
            this.state.logEntries.forEach((entry) => {
                let locationString = this.locationString(entry);
                if (entry.message.toLowerCase().indexOf(searchLower) === -1 &&
                    entry.level.indexOf(searchLower) === -1 &&
                    (!locationString || locationString.toLowerCase().indexOf(searchLower) === -1)) {
                    return;
                }
                rows.push(
                    <LogEntryRow
                        entry={entry}
                        search={searchLower}
                        location={locationString}
                        key={counter++}
                    />
                );
            });
        }
        return (
            <div>
                <form onSubmit={this.onSubmit} className={'form-inline'}>
                    <FormLabel length={1}>Log filter:</FormLabel>
                    <FormSelect value={this.state.treshold} name={'treshold'} onChange={this.handleTextChange}>
                        <option>fatal</option>
                        <option>error</option>
                        <option>warn</option>
                        <option>info</option>
                        <option>debug</option>
                        <option>trace</option>
                    </FormSelect>
                    <FormInput value={this.state.search} name={'search'} onChange={this.handleTextChange}/>
                    <FormSelect value={this.state.display} name={'display'} onChange={this.handleTextChange}>
                        <option>none</option>
                        <option>short</option>
                        <option>normal</option>
                    </FormSelect>
                    <FormSelect value={this.state.maxSize} name={'maxSize'} onChange={this.handleTextChange}>
                        <option>50</option>
                        <option>100</option>
                        <option>500</option>
                        <option>1000</option>
                        <option>10000</option>
                    </FormSelect>
                    <FormButton type={'submit'} bsStyle="success">load
                    </FormButton>
                </form>
                <Table striped bordered condensed hover>
                    <thead>
                    <tr>
                        <th>Timestamp</th>
                        <th>Log level</th>
                        {this.state.display !== 'none' ? <th>Location</th> : null}
                        <th>Message</th>
                    </tr>
                    </thead>
                    <tbody>{rows}</tbody>
                </Table>
            </div>
        );
    }
}

class LogEntryRow extends React.Component {
    render() {
        const entry = this.props.entry;
        var location = null;
        if (this.props.location) {
            location = <td><Highlight search={this.props.search}>{this.props.location}</Highlight></td>;
        }
        return (
            <tr>
                <td>{new Date(entry.timestamp).toISOString()}</td>
                <td className={`log-${entry.level}`}><Highlight search={this.props.search}>{entry.level}</Highlight>
                </td>
                {location}
                <td className={'tt'}><Highlight search={this.props.search}>{entry.message}</Highlight></td>
            </tr>
        );
    }
}

class LogViewer extends React.Component {

    render() {
        return (
            <div>
                <PageHeader>Log viewer</PageHeader>
                <LogViewerData/>
            </div>
        );
    }
}

export default LogViewer;

