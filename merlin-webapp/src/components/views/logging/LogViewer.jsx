import React from 'react';
import {PageHeader, Table} from 'react-bootstrap';
import {
    FormLabelField,
    FormButton, FormSelect, FormInput
} from "../../general/forms/FormComponents";
import {getRestServiceUrl} from "../../../actions/global";

class LogViewerData extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            search: '',
            treshold: 'info',
            maxSize: 50
        }
    }

    handleTextChange = event => {
        event.preventDefault();
        this.setState({[event.target.name]: event.target.value});
    }

    reload = event => {
        this.setState({
            isFetching: true,
            failed: false,
            logEntries: undefined
        });
        fetch(getRestServiceUrl("logging/query"), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                const logEntries = json.map(logEntry => {
                    return {
                        level: logEntry.level,
                        message: logEntry.message,
                        timestamp: logEntry.timestamp,
                        javaClass: logEntry.javaClass,
                        lineNumber: logEntry.lineNumber,
                        method: logEntry.method
                    };
                });
                this.setState({
                    isFetching: false,
                    logEntries
                })
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    }


    render() {
        const rows = [];
        if (this.state.logEntries) {
            let counter = 0;
            this.state.logEntries.forEach((entry) => {
                rows.push(
                    <LogEntryRow
                        entry={entry}
                        key={counter++}
                    />
                );
            });
        }
        return (
            <div>
                <form>
                    <FormLabelField label={'Log treshold'} name={'treshold'} fieldLength={2}>
                        <FormSelect value={this.state.treshold} onChange={this.handleTextChange}>
                            <option>fatal</option>
                            <option>error</option>
                            <option>warning</option>
                            <option>info</option>
                            <option>debug</option>
                            <option>trace</option>
                        </FormSelect>
                    </FormLabelField>
                    <FormLabelField label={'Search'} name={'search'} fieldLength={2}>
                        <FormInput value={this.state.search} onChange={this.handleTextChange}/>
                    </FormLabelField>
                    <FormButton onClick={this.reload} bsStyle="success">load
                    </FormButton>
                </form>
                <Table striped bordered condensed hover>
                    <thead>
                    <tr>
                        <th>Timestamp</th>
                        <th>Source</th>
                        <th>Log level</th>
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
        console.log(entry.message)
        const level = (entry.level === 'WARN' || entry.level === 'ERROR' || entry.logLevel === 'FATAL') ?
            <span style={{color: 'red'}}>
        {entry.level}
      </span> :
            entry.level
        ;
      const source = entry.javaClass + ':' + entry.method + ':' + entry.lineNumber;

        return (
            <tr>
                <td>{new Date(entry.timestamp).toISOString()}</td>
                <td>{source}</td>
                <td>{level}</td>
                <td>{entry.message}</td>
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

