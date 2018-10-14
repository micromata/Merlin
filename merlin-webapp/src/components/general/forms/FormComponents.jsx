import React from 'react';
import PropTypes from 'prop-types';
import {UncontrolledTooltip} from 'reactstrap';
import {revisedRandId} from '../../../actions/global';
import {IconInfo} from "../IconComponents";

// TODO: SPLIT IN DIFFERENT FILES

function FormGroup({validationState, children}) {
    return (
        <div className={`form-group row ${validationState ? `has-${validationState}` : ''}`}>
            {children}
        </div>
    );
}

FormGroup.propTypes = {
    validationState: PropTypes.oneOf(['success', 'warning', 'error', 'no-validation', null]),
    children: PropTypes.node
};

FormGroup.defaultProps = {
    validationState: null,
    children: null
};


function FormLabel({length, htmlFor, children}) {
    return (
        <label
            className={`col-sm-${length} col-form-label`}
            htmlFor={htmlFor}
        >
            {children}
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


function FormInput(props) {
    return (
        <input
            {...props}
            className={'form-control form-control-sm'}
        />
    );
}

FormInput.propTypes = {
    id: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    min: PropTypes.number,
    max: PropTypes.number,
    step: PropTypes.number,
    type: PropTypes.string,
    placeholder: PropTypes.string
};

FormInput.defaultProps = {
    id: null,
    name: '',
    value: '',
    min: null,
    max: null,
    step: 1,
    type: 'text',
    placeholder: ''
};


function FormSelect(props) {
    return (
        <select
            {...props}
            className={'form-control form-control-sm'}
        />
    );
}

FormSelect.propTypes = {
    id: PropTypes.string,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.bool]),
    name: PropTypes.string,
    onChange: PropTypes.func,
    children: PropTypes.node
};

FormSelect.defaultProps = {
    id: null,
    value: null,
    name: '',
    onChange: null,
    children: null
};


function FormCheckbox({id, name, checked, onChange, hint, label}) {
    if (!id) {
        id = revisedRandId();
    }
    let tooltip = null;
    if (hint) {
        tooltip =<span> <span id={`info-${id}`}><IconInfo/></span>
            <UncontrolledTooltip placement="right" target={`info-${id}`}>
                {hint}
            </UncontrolledTooltip></span>;
    }
    return (
        <div>
            <input
                className={'form-check-input'}
                type={'checkbox'}
                id={id}
                name={name}
                checked={checked}
                onChange={onChange}
            />
            <label
                className={'form-check-label'}
                htmlFor={id}
                style={{marginLeft: '1ex'}}
            >
                {label}
            </label>
            {tooltip}
        </div>
    );
}

FormCheckbox.propTypes = {
    id: PropTypes.string,
    name: PropTypes.string,
    checked: PropTypes.bool,
    onChange: PropTypes.func,
    hint: PropTypes.string,
    label: PropTypes.node
};

FormCheckbox.defaultProps = {
    id: null,
    name: '',
    checked: false,
    onChange: null,
    hint: '',
    label: ''
};


function FormField({id, hint, length, children}) {
    return (
        <div
            className={`col-sm-${length}`}
            id={id}
        >
            {children}
            {hint ? <small className={'text-muted'}>{hint}</small> : ''}
        </div>
    );
}

FormField.propTypes = {
    id: PropTypes.string,
    hint: PropTypes.node,
    length: PropTypes.number,
    children: PropTypes.node
};

FormField.defaultProps = {
    id: null,
    hint: null,
    length: 10,
    children: null
};


function FormLabelField({id, htmlFor, validationState, labelLength, fieldLength, label, hint, children}) {
    const forId = htmlFor || id || revisedRandId();
    return (
        <FormGroup validationState={validationState}>
            <FormLabel length={labelLength} htmlFor={forId}>
                {label}
            </FormLabel>
            <FormField length={fieldLength} hint={hint}>
                {React.cloneElement(children, {id: forId})}
            </FormField>
        </FormGroup>
    );
}

FormLabelField.propTypes = {
    id: PropTypes.string,
    htmlFor: PropTypes.string,
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null]),
    labelLength: PropTypes.number,
    fieldLength: PropTypes.number,
    label: PropTypes.node,
    hint: PropTypes.string,
    children: PropTypes.node
};

FormLabelField.defaultProps = {
    id: null,
    htmlFor: null,
    validationState: null,
    labelLength: 2,
    fieldLength: 10,
    label: '',
    hint: '',
    children: null
};


function FormLabelInputField({id = revisedRandId(), ...props}) {
    return (
        <FormLabelField
            htmlFor={id}
            labelLength={props.labelLength}
            label={props.label}
            hint={props.hint}
            validationState={props.validationState}
        >
            <FormInput
                id={id}
                name={props.name}
                type={props.type}
                min={props.min}
                max={props.max}
                step={props.step}
                value={props.value}
                onChange={props.onChange}
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
    hint: '',
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


function FormFieldset({id, text, children}) {
    return (
        <fieldset className={'form-group'} id={id}>
            <legend>{text}</legend>
            {children}
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


function FormButton({bsStyle = 'default', type, onClick, hint, disabled, children}) {
    return (
        <button
            type={type}
            className={`btn btn-${bsStyle}`}
            onClick={onClick}
            title={hint}
            disabled={disabled}
        >
            {children}
        </button>
    );
}

FormButton.propTypes = {
    bsStyle: PropTypes.oneOf(['default', 'danger', 'success', null]),
    type: PropTypes.string,
    onClick: PropTypes.func,
    hint: PropTypes.string,
    disabled: PropTypes.bool,
    children: PropTypes.node
};
FormButton.defaultProps = {
    bsStyle: 'default',
    type: 'button',
    onClick: null,
    hint: '',
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
