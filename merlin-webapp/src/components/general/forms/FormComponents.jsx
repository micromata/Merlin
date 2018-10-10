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
    length: PropTypes.number,
    htmlFor: PropTypes.string
};
FormLabel.defaultProps = {
    length: 2,
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
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    min: PropTypes.number,
    max: PropTypes.number,
    step: PropTypes.number,
    type: PropTypes.string,
    placeholder: PropTypes.string
};

class FormSelect extends React.Component {
    render() {
        return (
            <select className="form-control" id={this.props.id} value={this.props.value}
                    name={this.props.name}
                    onChange={this.props.onChange}>
                {this.props.children}
            </select>
        );
    }
}

FormSelect.propTypes = {
    id: PropTypes.string,
    name: PropTypes.string
};

class FormCheckbox extends React.Component {
    render() {
        let id = this.props.id ? this.props.id : revisedRandId();
        return (
            <div>
                <input className="form-check-input" type="checkbox"
                       checked={this.props.checked}
                       id={id} name={this.props.name}
                       onChange={this.props.onChange}
                       title={this.props.hint}/>
                <label className="form-check-label" htmlFor={id} style={{marginLeft: 1 + 'ex'}}
                       title={this.props.title}>
                    {this.props.label}
                </label>
            </div>
        );
    }
}

FormCheckbox.propTypes = {
    id: PropTypes.string,
    name: PropTypes.string,
    hint: PropTypes.string,
    checked: PropTypes.bool,
    label: PropTypes.node
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
    length: PropTypes.number,
    hint: PropTypes.node,
    id: PropTypes.string
};
FormField.defaultProps = {
    length: 10
};

class FormLabelField extends React.Component {
    render() {
        let id = this.props.htmlFor ? this.props.htmlFor : (this.props.id ? this.props.id : revisedRandId());
        return (
            <FormGroup validationState={this.props.validationState}>
                <FormLabel length={this.props.labelLength} htmlFor={id}>
                    {this.props.label}
                </FormLabel>
                <FormField length={this.props.fieldLength} hint={this.props.hint}>
                    {React.cloneElement(this.props.children, { id: id })}
                </FormField>
            </FormGroup>
        );
    }
};
FormLabelField.propTypes = {
    label: PropTypes.node,
    labelLength: PropTypes.number,
    fieldLength: PropTypes.number,
    hint: PropTypes.string,
    id: PropTypes.string,
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null])
};

class FormLabelInputField extends React.Component {
    render() {
        let id = this.props.id ? this.props.id : revisedRandId();
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
};
FormLabelInputField.propTypes = {
    id: PropTypes.string,
    label: PropTypes.node,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    field: PropTypes.node,
    name: PropTypes.string,
    type: PropTypes.string,
    min: PropTypes.number,
    max: PropTypes.number,
    step: PropTypes.number,
    placeholder: PropTypes.string,
    labelLength: PropTypes.number,
    fieldLength: PropTypes.number,
    validationState: PropTypes.oneOf(['success', 'warning', 'error', null])
};
FormLabelInputField.defaultProps = {
    labelLength: 2,
    fieldLength: 10
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

class FormButton extends React.Component {
    render() {
        let bsStyle = this.props.bsStyle ? 'btn-' + this.props.bsStyle : '';
        return (
            <button type="button" className={`btn ${bsStyle}`} onClick={this.props.onClick}
                    title={this.props.hint} disabled={this.props.disabled}>
                {this.props.children}
            </button>
        );
    }
};
FormButton.propTypes = {
    hint: PropTypes.string,
    disabled: PropTypes.bool,
    bsStyle: PropTypes.oneOf(['danger', 'success', null])
};
FormButton.defaultProps = {
    disabled: false
};

export {FormGroup, FormLabel, FormField, FormLabelField, FormInput, FormSelect, FormCheckbox, FormLabelInputField, FormFieldset, FormButton};
