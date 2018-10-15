import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Form} from 'reactstrap';
import {saveTemplateRunConfiguration} from '../../../actions';
import {getResponseHeaderFilename, getRestServiceUrl} from '../../../utilities/global';
import downloadFile from '../../../utilities/download';
import {FormButton, FormInput, FormLabelField, FormSelect} from '../../general/forms/FormComponents';

class TemplateRunTab extends React.Component {

    runConfiguration = this.props.runConfigurations[this.props.primaryKey] ||
        this.props.variableDefinitions.reduce((accumulator, current) => ({
            ...accumulator,
            [current.name ? current.name : current]: current.allowedValuesList &&
            current.allowedValuesList.length !== 0 ? current.allowedValuesList[0] : ''
        }), {});
    runTemplate = (endpoint, headers, body) => {
        let filename;

        fetch(endpoint, {
            method: 'POST',
            headers,
            body: body
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                filename = getResponseHeaderFilename(response.headers.get('Content-Disposition'));
                return response.blob();
            })
            .then(blob => downloadFile(blob, filename))
            .catch(alert);
    };
    runSingleTemplate = event => {
        event.preventDefault();

        this.runTemplate(getRestServiceUrl('templates/run'), {
                'Content-Type': 'application/json'
            },
            JSON.stringify({
                // TODO USAGE OF templateDefinitionId
                templateDefinitionId: this.props.templateDefinitionId,
                templatePrimaryKey: this.props.primaryKey,
                variables: this.runConfiguration
            }))
    };
    handleVariableChange = event => {
        event.preventDefault();

        this.runConfiguration[event.target.name] = event.target.value;
        this.props.saveTemplateRunConfiguration(this.props.primaryKey, this.runConfiguration);
    };

    render() {
        let valid = true;

        return (
            <React.Fragment>
                <h4>Single Generation:</h4>
                <Form onSubmit={this.runSingleTemplate}>
                    {this.props.variableDefinitions.map(item => {
                        let formControl;
                        const name = item.name ? item.name : item;
                        const formControlProps = {
                            name,
                            value: this.runConfiguration[name],
                            onChange: this.handleVariableChange
                        };
                        let validationMessage;

                        if (typeof item === 'string') {
                            formControl = <FormInput
                                {...formControlProps}
                                type={'text'}
                            />
                        } else if (item.allowedValuesList && item.allowedValuesList.length !== 0) {
                            formControl = <FormSelect
                                {...formControlProps}
                            >
                                {item.allowedValuesList.map(option => <option
                                    key={`template-run-variable-${item.refId}-select-${option}`}
                                    value={option}
                                >
                                    {option}
                                </option>)}
                            </FormSelect>;
                        } else {

                            if (item.required && formControlProps.value.trim() === '') {
                                validationMessage = 'This field is required. Please insert a value.';
                            } else if (item.type === 'INT' && isNaN(formControlProps.value)) {
                                validationMessage = 'Value must be a number.';
                            } else if (item.minimumValue && item.minimumValue > Number(formControlProps.value)) {
                                validationMessage = `The field must be above  ${item.minimumValue}.`;
                            } else if (item.maximumValue && item.maximumValue < Number(formControlProps.value)) {
                                validationMessage = `The field must be below ${item.maximumValue}.`;
                            }

                            if (validationMessage) {
                                valid = false;
                                formControlProps.invalid = true;
                            } else {
                                formControlProps.valid = true;
                            }

                            if (item.minimumValue) {
                                formControlProps.min = item.minimumValue;
                            }

                            if (item.maximumValue) {
                                formControlProps.max = item.maximumValue;
                            }

                            if (item.type === 'INT') {
                                item.type = 'number';
                            }
                            var { fieldLength, ...other } = formControlProps;
                            formControl = <FormInput
                                {...other}
                                type={item.type}
                            />;
                        }

                        return <FormLabelField
                            label={name}
                            labelLength={3}
                            fieldLength={9}
                            key={`template-run-variable-${item.refId ? item.refId : item}`}
                            hint={item.description ? item.description : ''}
                            validationMessage={validationMessage}
                        >
                            {formControl}
                        </FormLabelField>;
                    })}
                    <FormButton
                        bsStyle={'success'}
                        type={'submit'}
                        disabled={!valid}
                    >
                        Run
                    </FormButton>
                </Form>
            </React.Fragment>
        );
    }
}

TemplateRunTab.propTypes = {
    runConfigurations: PropTypes.object.isRequired,
    saveTemplateRunConfiguration: PropTypes.func.isRequired,
    primaryKey: PropTypes.string.isRequired,
    templateDefinitionId: PropTypes.string.isRequired,
    variableDefinitions: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.shape({
        refId: PropTypes.number,
        name: PropTypes.string,
        description: PropTypes.string,
        required: PropTypes.bool,
        unique: PropTypes.bool,
        allowedValuesList: PropTypes.arrayOf(PropTypes.string),
        type: PropTypes.string
    })), PropTypes.array]).isRequired
};

const mapStateToProps = state => ({
    runConfigurations: state.template.runConfigurations
});

const actions = {
    saveTemplateRunConfiguration
};

export default connect(mapStateToProps, actions)(TemplateRunTab);