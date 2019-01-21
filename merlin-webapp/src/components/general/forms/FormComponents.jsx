import React from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import {FormFeedback, Input, UncontrolledTooltip} from 'reactstrap';
import {FormCheckbox} from "./FormCheckbox";
import {FormButton} from "./FormButton";
import {FormSelect, FormOption} from "./FormSelect";
import {revisedRandId} from "../../../utilities/global";
import I18n from "../translation/I18n";

const FormGroup = (props) => {
    const {className, ...other} = props;
    return (
        <div className={classNames('form-group row', className)}
             {...other}
        >
            {props.children}
        </div>
    );
}

FormGroup.propTypes = {
    children: PropTypes.node,
    className: PropTypes.string
};

FormGroup.defaultProps = {
    children: null
};

const FormRow = (props) => {
    const {className, ...other} = props;
    return (
        <div className={classNames('form-row', className)}
             {...other}
        >
            {props.children}
        </div>
    );
}

FormGroup.propTypes = {
    children: PropTypes.node,
    className: PropTypes.string
};

FormGroup.defaultProps = {
    children: null
};

const FormLabel = (props) => {
    const {className, i18nKey, length, ...other} = props;
    return (
        <label className={classNames(`col-sm-${props.length} col-form-label`, className)}
               {...other}
        >
            {i18nKey ? <I18n name={i18nKey}/> : props.children}
        </label>
    );
}

FormLabel.propTypes = {
    length: PropTypes.number,
    i18nKey: PropTypes.string,
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
    const {fieldLength, className, hint, hintPlacement, id, ...other} = props;
    return (
        <React.Fragment>
            <Input id={targetId}
                   className={classNames(`col-sm-${props.fieldLength}`, className)}
                   {...other}
            />
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


const FormField = (props) => {
    const {className, id, validationMessage, ...other} = props;
    return (
        <div id={props.id}
             className={classNames(`col-sm-${props.length}`, className)}
             {...other}
        >
            {props.children}
            {validationMessage ? <FormFeedback>{validationMessage}</FormFeedback> : ''}
            {props.hint ? <small className={'text-muted'}>{props.hint}</small> : ''}
        </div>
    );
}

FormField.propTypes = {
    id: PropTypes.string,
    hint: PropTypes.node,
    length: PropTypes.number,
    validationMessage: PropTypes.string,
    children: PropTypes.node,
    className: PropTypes.string
};

FormField.defaultProps = {
    id: null,
    hint: null,
    length: 10,
    validationMessage: null,
    children: null
};


const FormLabelField = (props) => {
    const forId = props.children.props.id || props.children.props.name || props.htmlFor || revisedRandId();
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
        <fieldset id={props.id}
                  className={classNames('form-group', props.className)}
        >
            <legend>{props.text}</legend>
            {props.children}
        </fieldset>
    );
}

FormFieldset.propTypes = {
    id: PropTypes.string,
    text: PropTypes.node,
    children: PropTypes.node,
    className: PropTypes.string
};

FormFieldset.defaultProps = {
    id: null,
    text: '',
    children: null
};

export {
    FormGroup,
    FormLabel,
    FormField,
    FormLabelField,
    FormInput,
    FormSelect,
    FormOption,
    FormCheckbox,
    FormLabelInputField,
    FormFieldset,
    FormButton,
    FormRow
};
