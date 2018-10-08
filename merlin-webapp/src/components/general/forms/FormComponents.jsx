import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import {revisedRandId} from "../../../actions/global";

class FormLabelField extends React.Component {
    render() {
        let validationClass = '';
        if (this.props.validationState) {
            validationClass = " has-" + this.props.validationState;
        }
        let hint = '';
        if (this.props.hint) {
            hint = <small className="text-muted">{this.props.hint}</small>
        }
        return (
            <div className={`form-group row ${validationClass}`}>
                <label className={`col-sm-${this.props.labelLength} col-form-label`}
                       htmlFor={this.props.htmlFor}>{this.props.label}</label>
                <div className={`col-sm-${this.props.fieldLength}`}>{this.props.field}
                    {hint}
                </div>
            </div>
        );
    }
};
FormLabelField.propTypes = {
    label: PropTypes.node,
    field: PropTypes.node,
    labelLength: PropTypes.string,
    fieldLength: PropTypes.string,
    htmlFor: PropTypes.string,
    hint: PropTypes.string,
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null])
};
FormLabelField.defaultProps = {
    labelLength: '2',
    fieldLength: '10',
};

class FormLabelInputField extends React.Component {
    render() {
        let id = revisedRandId();
        return (
            <FormLabelField label={this.props.label} htmlFor={id} labelLength={this.props.labelLength}
                            fieldLength={this.props.fieldLength} hint={this.props.hint}
                            validationState={this.props.validationState}
                            field={<input id={id} type={this.props.type} min={this.props.min} max={this.props.max}
                                          step={this.props.step}
                                          value={this.props.value} onChange={this.props.onChange}
                                          placeholder={this.props.placeholder}/>}/>
        );
    }
};
FormLabelInputField.propTypes = {
    label: PropTypes.node,
    field: PropTypes.node,
    type: PropTypes.string,
    min: PropTypes.string,
    max: PropTypes.string,
    step: PropTypes.string,
    placeholder: PropTypes.string,
    onChange: PropTypes.node,
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
                {this.props.content}
            </fieldset>
        );
    }
};
FormFieldset.propTypes = {
    text: PropTypes.string,
    content: PropTypes.node
};

export {FormLabelField, FormLabelInputField, FormFieldset};
