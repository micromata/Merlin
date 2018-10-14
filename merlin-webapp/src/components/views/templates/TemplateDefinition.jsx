import React from 'react';
import {getRestServiceUrl, isDevelopmentMode} from "../../../actions/global";
import {Form} from 'reactstrap';
import ErrorAlert from "../../general/ErrorAlert";
import {FormGroup, FormField, FormLabel, FormCheckbox} from "../../general/forms/FormComponents";
import {PageHeader} from "../../general/BootstrapComponents";
import EditableTextField from "../../general/forms/EditableTextField";

class TemplateDefinition extends React.Component {
    state = {
        primaryKey: this.props.match.params.primaryKey,
        definition: null
    };

    componentDidMount = () => {
        this.fetchTemplateDefinition();
    };

    handleTextChange = (value, name) => {
        let definition = this.state.definition;
        definition[name] = value;
        this.setState({definition: definition});
    }

    handleStateChange = event => {
        let definition = this.state.definition;
        definition[event.target.name] = event.target.checked;
        this.setState({definition: definition});
    }

    fetchTemplateDefinition = () => {
        this.setState({
            isFetching: true,
            failed: false,
            definition: undefined
        });
        fetch(getRestServiceUrl("templates/definition", {
            primaryKey: this.state.primaryKey
        }), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                this.setState({
                    isFetching: false,
                    definition: json
                });
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    };

    render = () => {
        let content = undefined;
        if (this.state.isFetching) {
            content = <i>Loading...</i>;
        } else if (this.state.failed) {
            content = <ErrorAlert
                title={'Cannot load Template Definition'}
                description={'Something went wrong during contacting the rest api.'}
                action={{
                    handleClick: this.fetchTemplateDefinition,
                    title: 'Try again'
                }}
            />;
        } else if (this.state.definition) {
            content = <div>
                <Form>
                    <FormGroup>
                        <FormLabel htmlFor={'id'}>
                            Id
                        </FormLabel>
                        <FormField length={6}>
                            <EditableTextField
                                type={'text'}
                                value={this.state.definition.id}
                                name={'id'}
                                onChange={this.handleTextChange}
                            />
                        </FormField>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel htmlFor={'description'}>
                            Description
                        </FormLabel>
                        <FormField length={6}>
                            <EditableTextField
                                type={'textarea'}
                                value={this.state.definition.description}
                                name={'description'}
                                onChange={this.handleTextChange}
                            />
                        </FormField>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel htmlFor={'filenamePattern'}>
                            File name pattern
                        </FormLabel>
                        <FormField length={6}>
                            <EditableTextField
                                type={'text'}
                                value={this.state.definition.filenamePattern}
                                name={'filenamePattern'}
                                onChange={this.handleTextChange}
                            />
                        </FormField>
                        <FormField length={2}>
                            <FormCheckbox checked={this.state.definition.stronglyRestrictedFilenames}
                                          name="stronglyRestrictedFilenames" label={'stronglyRestrictedFilenames'}
                                          onChange={this.handleStateChange}
                                          hint="Merlin will ensure filenames without unallowed chars. If checked, Merlin will only use ASCII-chars and replace e. g. ä by ae (recommended)."/>
                        </FormField>
                    </FormGroup>
                </Form>
            </div>;
        }

        let todo = '';
        if (isDevelopmentMode()) {
            todo = <div><h3>ToDo</h3>
                <ul>
                    <li>Auto grow of textarea.</li>
                </ul>
            </div>
        }


        return <div>
            <PageHeader>
                Template definition
            </PageHeader>
            {content}
            {todo}
        </div>;
    };

    constructor(props) {
        super(props);
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleStateChange = this.handleStateChange.bind(this);
        this.fetchTemplateDefinition = this.fetchTemplateDefinition.bind(this);
    }
}

export default TemplateDefinition;