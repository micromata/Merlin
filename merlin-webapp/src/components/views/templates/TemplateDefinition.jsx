import React from 'react';
import {getRestServiceUrl} from "../../../actions/global";
import {Form} from 'reactstrap';
import ErrorAlert from "../../general/ErrorAlert";
import {
    FormGroup,
    FormLabelField,
    FormLabelInputField,
    FormFieldset,
    FormField, FormButton, FormSelect, FormLabel
} from "../../general/forms/FormComponents";
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
                                onChange={this.handleChange}
                            />
                        </FormField>
                    </FormGroup>
                </Form>
            </div>;
        }

        return <div>
            <PageHeader>
                Template definition
            </PageHeader>
            {content}
        </div>;
    };

    constructor(props) {
        super(props);
        this.fetchTemplateDefinition = this.fetchTemplateDefinition.bind(this);
    }
}

export default TemplateDefinition;