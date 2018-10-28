import React from 'react';
import {Table} from 'reactstrap';
import {arrayNotEmpty} from '../../../utilities/global';
import {IconCollapseClose, IconCollapseOpen} from "../../general/IconComponents";
import I18n from "../../general/translation/I18n";

class TemplateStatistics extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showAllVariableTypes: false
        }
    }

    renderConditional = (conditional) => {
        const statement = conditional.conditionalStatement;
        let childConditionals = null;
        if (arrayNotEmpty(conditional.childConditionals)) {
            childConditionals = (<ul>{conditional.childConditionals.map((child, index) => {
                // Return the element. Also pass key
                return (<li key={index}>{this.renderConditional(child)}</li>)
            })}</ul>)
        }
        return (<React.Fragment>
            <span className={'badge badge-success'}>{statement}</span>
            {childConditionals}
        </React.Fragment>)
    }

    getVariableColor = (variable) => {
        if (!this.props.statistics.allDefinedVariables)
            return "info";
        if (this.props.statistics.masterVariables.indexOf(variable) >= 0)
            return "danger";
        if (this.props.statistics.unusedVariables.indexOf(variable) >= 0)
            return "light";
        if (this.props.statistics.dependentVariables.indexOf(variable) >= 0)
            return "secondary";
        if (this.props.statistics.allDefinedVariables.indexOf(variable) >= 0)
            return "success";
        return "warning";
    }

    toggleVariables = (event) => {
        event.preventDefault();
        this.setState({showAllVariableTypes: !this.state.showAllVariableTypes});
    }

    render() {
        let caretIcon = this.state.showAllVariableTypes ? <IconCollapseClose/> : <IconCollapseOpen/>;
        let allVariableTypes = null;
        if (this.state.showAllVariableTypes) {
            allVariableTypes = <React.Fragment>
                <tr>
                    <td><I18n name={'templates.variables.used'} /></td>
                    <td>
                        {this.props.statistics.usedVariables.map((variable, index) => {
                            // Return the element. Also pass key
                            return (<React.Fragment key={index}>
                                <span
                                    className={`badge badge-${this.getVariableColor(variable)}`}>{variable}</span>{' '}
                            </React.Fragment>)
                        })}</td>
                </tr>
                {arrayNotEmpty(this.props.statistics.unusedVariables) ?
                    <tr>
                        <td><I18n name={'templates.variables.unused'}/></td>
                        <td>
                            {this.props.statistics.unusedVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                    <span
                                        className={`badge badge-${this.getVariableColor(variable)}`}>{variable}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    : null}
                {arrayNotEmpty(this.props.statistics.allDefinedVariables) ?
                    <tr>
                        <td><I18n name={'templates.variables.defined'}/></td>
                        <td>
                            {this.props.statistics.allDefinedVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                        <span className={`badge badge-${this.getVariableColor(variable)}`}
                                              key={index}>{variable}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    : null}
                {arrayNotEmpty(this.props.statistics.masterVariables) ?
                    <tr>
                        <td><I18n name={'templates.variables.master'}/></td>
                        <td>
                            {this.props.statistics.masterVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                        <span className={`badge badge-${this.getVariableColor(variable)}`}
                                              key={index}>{variable}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    : null}
                {arrayNotEmpty(this.props.statistics.dependentVariables) ?
                    <tr>
                        <td><I18n name={'templates.variables.dependent'}/></td>
                        <td>
                            {this.props.statistics.dependentVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                        <span className={`badge badge-${this.getVariableColor(variable)}`}
                                              key={index}>{variable}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    : null}
                {arrayNotEmpty(this.props.statistics.undefinedVariables) ?
                    <tr>
                        <td><I18n name={'templates.variables.undefined'}/></td>
                        <td>
                            {this.props.statistics.undefinedVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                        <span className={`badge badge-${this.getVariableColor(variable)}`}
                                              key={index}>{variable}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    : null}
                {arrayNotEmpty(this.props.statistics.conditionals.conditionalsSet) ?
                    <tr>
                        <td><I18n name={'templates.conditionals'}/></td>
                        <td>
                            {this.props.statistics.conditionals.conditionalsSet.map((conditional, index) => {
                                // Return the element. Also pass key
                                return (<li key={index}>{this.renderConditional(conditional)}</li>)
                            })}</td>
                    </tr>
                    : null}
            </React.Fragment>
        }

        return (
            <React.Fragment>
                <h4><I18n name={'templates.statistics'}/></h4>
                <Table hover>
                    <tbody>
                    <tr>
                        <td style={{whiteSpace: 'nowrap'}}><I18n name={'templates.variables.input'}/>{' '}
                            <button onClick={this.toggleVariables} type="button"
                                    className="btn btn-link btn-outline-primary btn-sm">{caretIcon}</button>
                        </td>
                        <td>
                            {this.props.statistics.inputVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                    <span
                                        className={`badge badge-${this.getVariableColor(variable.name)}`}>{variable.name}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    {allVariableTypes}
                    </tbody>
                </Table>
            </React.Fragment>
        );
    }
}

export default TemplateStatistics;