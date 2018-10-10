import React from 'react';
import {PageHeader, Collapse, Table} from 'react-bootstrap';
import {
    FormGroup,
    FormLabelField,
    FormLabelInputField,
    FormFieldset,
    FormField, FormButton, FormSelect, FormInput
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
        var filter = {search: this.state.search, treshold: this.state.treshold, maxSize: this.props.maxSize};
        fetch(getRestServiceUrl("logging/query"), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(filter)
        })
            .then(response => response.json())
            .then(json => {
                const logEntries = json.templates.map(logEntry => {
                    return {
                        logLevel: logEntry.logLevel,
                        message: logEntry.message,
                        javaClass: logEntry.javaClass
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
        this.props.logEntries.forEach((entry) => {
            rows.push(
                <LogEntryRow
                    entry={entry}
                    key={entry.timestamp}
                />
            );
        });
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
        const entry = this.props.logEntry;
        const level = entry.level < 3 ?
            level.level :
            <span style={{color: 'red'}}>
        {entry.level}
      </span>;

        return (
            <tr>
                <td>{entry.timestamp}</td>
                <td>{entry.source}</td>
                <td>{entry.level}</td>
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

