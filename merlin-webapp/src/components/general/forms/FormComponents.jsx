import React from 'react';
import PropTypes from 'prop-types';
import {FormFeedback, Input, UncontrolledTooltip} from 'reactstrap';
import {FormCheckbox} from "./FormCheckbox";
// TODO: SPLIT IN DIFFERENT FILES

const FormGroup = (props) => {
    return (
        <div className={`form-group row`}>
            {props.children}
        </div>
    );
}

FormGroup.propTypes = {
    children: PropTypes.node
};

FormGroup.defaultProps = {
    children: null
};


const FormLabel = (props) => {
    return (
        <label
            className={`col-sm-${props.length} col-form-label`}
            htmlFor={props.htmlFor}
        >
            {props.children}
        </label>
    );
}

FormLabel.propTypes = {
    length: PropTypes.number,
    htmlFor: PropTypes.string
};

FormLabel.defaultProps = {
    length: 2,
    htmlFor: null
};


const FormInput = (props) => {
    let tooltip = null;
    let targetId = props.id || props.name;
    if (props.hint) {
        tooltip = <UncontrolledTooltip placement={props.hintPlacement} target={targetId}>
            {props.hint}
        </UncontrolledTooltip>;
    }
    var {fieldLength, className, hint, hintPlacement, id, ...other} = props;
    return (
        <React.Fragment>
            <Input
                {...other}
                id={targetId}
                className={`col-sm-${props.fieldLength} ${props.className}`}/>
            {tooltip}
        </React.Fragment>
    );
}

FormInput.propTypes = {
    id: PropTypes.string,
    name: PropTypes.string,
    hint: PropTypes.string,
    hintPlacement: PropTypes.oneOf(['right', 'top']),
    fieldLength: PropTypes.number,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    min: PropTypes.number,
    max: PropTypes.number,
    step: PropTypes.number,
    type: PropTypes.string,
    placeholder: PropTypes.string,
    valid: PropTypes.bool,
    invalid: PropTypes.bool,
    className: PropTypes.string
};

FormInput.defaultProps = {
    id: null,
    name: '',
    hint: null,
    hintPlacement: 'top',
    fieldLength: 10,
    value: '',
    min: null,
    max: null,
    step: 1,
    type: 'text',
    placeholder: '',
    valid: null,
    invalid: null
};


const FormSelect = (props) => {
    let tooltip = null;
    let targetId = props.id || props.name;
    if (props.hint) {
        tooltip = <UncontrolledTooltip placement={props.hintPlacement} target={targetId}>
            {props.hint}
        </UncontrolledTooltip>;
    }
    var {hint, hintPlacement, id, ...other} = props;
    return (
        <React.Fragment>
            <select
                {...other}
                id={targetId}
                className={'custom-select form-control form-control-sm mr-1'}
            >
                {props.children}
            </select>
            {tooltip}
        </React.Fragment>
    );
}

FormSelect.propTypes = {
    id: PropTypes.string,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.bool]),
    name: PropTypes.string,
    onChange: PropTypes.func,
    hint: PropTypes.string,
    hintPlacement: PropTypes.oneOf(['right', 'top']),
    children: PropTypes.node
};

FormSelect.defaultProps = {
    hint: null,
    hintPlacement: 'top',
};

const FormField = (props) => {
    return (
        <div
            className={`col-sm-${props.length}`}
            id={props.id}
        >
            {props.children}
            {props.validationMessage ? <FormFeedback>{props.validationMessage}</FormFeedback> : ''}
            {props.hint ? <small className={'text-muted'}>{props.hint}</small> : ''}
        </div>
    );
}

FormField.propTypes = {
    id: PropTypes.string,
    hint: PropTypes.node,
    length: PropTypes.number,
    validationMessage: PropTypes.string,
    children: PropTypes.node
};

FormField.defaultProps = {
    id: null,
    hint: null,
    length: 10,
    validationMessage: null,
    children: null
};


const FormLabelField = (props) => {
    const forId = props.children.props.id || props.htmlFor;
    return (
        <FormGroup>
            <FormLabel length={props.labelLength} htmlFor={forId}>
                {props.label}
            </FormLabel>
            <FormField length={props.fieldLength} hint={props.hint} validationMessage={props.validationMessage}>
                {React.cloneElement(props.children, {id: forId})}
            </FormField>
        </FormGroup>
    );
}

FormLabelField.propTypes = {
    id: PropTypes.string,
    htmlFor: PropTypes.string,
    validationMessage: PropTypes.string,
    labelLength: PropTypes.number,
    fieldLength: PropTypes.number,
    label: PropTypes.node,
    hint: PropTypes.string,
    children: PropTypes.node
};

FormLabelField.defaultProps = {
    id: null,
    htmlFor: null,
    validationMessage: null,
    labelLength: 2,
    fieldLength: 10,
    label: '',
    hint: '',
    children: null
};


const FormLabelInputField = (props) => {
    return (
        <FormLabelField
            htmlFor={props.id}
            labelLength={props.labelLength}
            label={props.label}
            hint={props.hint}
            validationState={props.validationState}
        >
            <FormInput
                id={props.id}
                name={props.name}
                type={props.type}
                min={props.min}
                hint={props.hint}
                hintPlacement={props.hintPlacement}
                max={props.max}
                step={props.step}
                value={props.value}
                onChange={props.onChange}
                fieldLength={props.fieldLength}
                placeholder={props.placeholder}
            />
        </FormLabelField>
    );
}

FormLabelInputField.propTypes = {
    id: PropTypes.string,
    label: PropTypes.node,
    labelLength: PropTypes.number,
    fieldLength: PropTypes.number,
    hint: PropTypes.string,
    hintPlacement: PropTypes.oneOf(['right', 'top']),
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null]),
    type: PropTypes.string,
    name: PropTypes.string,
    min: PropTypes.number,
    max: PropTypes.number,
    step: PropTypes.number,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    onChange: PropTypes.func,
    placeholder: PropTypes.string
};

FormLabelInputField.defaultProps = {
    id: null,
    label: '',
    labelLength: 2,
    fieldLength: 10,
    hint: null,
    hintPlacement: 'top',
    validationState: null,
    type: 'text',
    name: '',
    min: null,
    max: null,
    step: 1,
    value: '',
    onChange: null,
    placeholder: ''
};


const FormFieldset = (props) => {
    return (
        <fieldset className={'form-group'} id={props.id}>
            <legend>{props.text}</legend>
            {props.children}
        </fieldset>
    );
}

FormFieldset.propTypes = {
    id: PropTypes.string,
    text: PropTypes.string,
    children: PropTypes.node
};

FormFieldset.defaultProps = {
    id: null,
    text: '',
    children: null
};


const FormButton = (props) => {
    let tooltip = null;
    let targetId = props.id || props.name;
    /*    if (props.hint) {
            tooltip = <UncontrolledTooltip placement={props.hintPlacement} target={targetId}>
                {props.hint}
            </UncontrolledTooltip>;
        }*/
    var {className, hint, hintPlacement, id, ...other} = props;
    return (
        <React.Fragment>
            <button
                {...other}
                id={targetId}
                className={`btn btn-${props.bsStyle}`}
            >
                {props.children}
            </button>
            {tooltip}
        </React.Fragment>
    );
}

FormButton.propTypes = {
    id: PropTypes.string,
    bsStyle: PropTypes.oneOf(['primary', 'outline-primary', null]),
    type: PropTypes.string,
    onClick: PropTypes.func,
    hint: PropTypes.string,
    hintPlacement: PropTypes.oneOf(['right', 'top']),
    disabled: PropTypes.bool,
    children: PropTypes.node
};
FormButton.defaultProps = {
    bsStyle: 'outline-primary',
    type: 'button',
    onClick: null,
    hint: null,
    hintPlacement: 'top',
    disabled: false,
    children: null
};

export {
    FormGroup,
    FormLabel,
    FormField,
    FormLabelField,
    FormInput,
    FormSelect,
    FormCheckbox,
    FormLabelInputField,
    FormFieldset,
    FormButton
};
