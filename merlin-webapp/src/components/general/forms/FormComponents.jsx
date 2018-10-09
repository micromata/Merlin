import React from 'react';
import PropTypes from 'prop-types';
import {revisedRandId} from "../../../actions/global";

class FormGroup extends React.Component {
    render() {
        let validationClass = '';
        if (this.props.validationState) {
            validationClass = " has-" + this.props.validationState;
        }
        return (<div className={`form-group row ${validationClass}`}>
                {this.props.children}
            </div>
        );
    }
}

FormGroup.propTypes = {
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null])
};

class FormLabel extends React.Component {
    render() {
        return (
            <label className={`col-sm-${this.props.length} col-form-label`}
                       htmlFor={this.props.htmlFor}>{this.props.children}</label>
        );
    }
}
FormLabel.propTypes = {
    length: PropTypes.string,
    htmlFor: PropTypes.string
};
FormLabel.defaultProps = {
    length: '2',
};

class FormInput extends React.Component {
    render() {
        return (
            <input id={this.props.id} name={this.props.name} type={this.props.type} min={this.props.min}
                   max={this.props.max} className={'form-control'}
                   step={this.props.step}
                   value={this.props.value} onChange={this.props.onChange}
                   placeholder={this.props.placeholder}/>
        );
    }
}
FormInput.propTypes = {
    id: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.string,
    min: PropTypes.string,
    max: PropTypes.string,
    stemp: PropTypes.string,
    type: PropTypes.string,
    onChange: PropTypes.string,
    placeholder: PropTypes.string,
    htmlFor: PropTypes.string
};
FormInput.defaultProps = {
    length: '10',
};

class FormField extends React.Component {
    render() {
        let hint = '';
        if (this.props.hint) {
            hint = <small className="text-muted">{this.props.hint}</small>
        }
        return (
            <div className={`col-sm-${this.props.length}`} id={this.props.id}>
                {this.props.children}
                {hint}
            </div>
        );
    }
}
FormField.propTypes = {
    length: PropTypes.string,
    hint: PropTypes.node,
    id: PropTypes.string
};
FormField.defaultProps = {
    length: '10',
};

class FormLabelField extends React.Component {
    render() {
        return (
            <FormGroup validationState={this.props.validationState}>
                <FormLabel length={this.props.labelLength} htmlFor={this.props.htmlFor}>
                    {this.props.label}
                </FormLabel>
                <FormField length={this.props.fieldLength} hint={this.props.hint}>
                    {this.props.children}
                </FormField>
            </FormGroup>
        );
    }
};
FormLabelField.propTypes = {
    label: PropTypes.node,
    labelLength: PropTypes.string,
    fieldLength: PropTypes.string,
    htmlFor: PropTypes.string,
    hint: PropTypes.string,
    id: PropTypes.string,
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null])
};

class FormLabelInputField extends React.Component {
    render() {
        let id = revisedRandId();
        return (
            <FormLabelField label={this.props.label} htmlFor={id} labelLength={this.props.labelLength}
                            fieldLength={this.props.fieldLength} hint={this.props.hint}
                            validationState={this.props.validationState}>
                <FormInput id={id} name={this.props.name} type={this.props.type} min={this.props.min}
                       max={this.props.max}
                       step={this.props.step}
                       value={this.props.value} onChange={this.props.onChange}
                       placeholder={this.props.placeholder}/>
            </FormLabelField>
        );
    }

    constructor(props) {
        super(props);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleInputChange = event => {
        this.props.onInputChange(event);
    }
};
FormLabelInputField.propTypes = {
    label: PropTypes.node,
    field: PropTypes.node,
    name: PropTypes.string,
    type: PropTypes.string,
    min: PropTypes.string,
    max: PropTypes.string,
    step: PropTypes.string,
    placeholder: PropTypes.string,
    labelLength: PropTypes.string,
    fieldLength: PropTypes.string,
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null])
};
FormLabelInputField.defaultProps = {
    labelLength: '2',
    fieldLength: '10',
};

class FormFieldset extends React.Component {
    render() {
        return (
            <fieldset className="form-group" id={this.props.id}>
                <legend>{this.props.text}</legend>
                {this.props.children}
            </fieldset>
        );
    }
};
FormFieldset.propTypes = {
    text: PropTypes.string
};

export {FormGroup, FormLabel, FormField, FormLabelField, FormInput, FormLabelInputField, FormFieldset};
