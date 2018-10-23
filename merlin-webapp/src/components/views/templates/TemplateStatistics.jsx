import React from 'react';
import {Table} from 'reactstrap';
import {arrayNotEmpty} from '../../../utilities/global';

class TemplateStatistics extends React.Component {

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

    render() {
        return (
            <React.Fragment>
                <h4>Statistics</h4>
                <Table hover>
                    <tbody>
                    <tr>
                        <td>Input variables</td>
                        <td>
                            {this.props.statistics.inputVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                    <span className={`badge badge-${this.getVariableColor(variable.name)}`}>{variable.name}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    <tr>
                        <td>Used variables</td>
                        <td>
                            {this.props.statistics.usedVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (<React.Fragment key={index}>
                                    <span className={`badge badge-${this.getVariableColor(variable)}`}>{variable}</span>{' '}
                                </React.Fragment>)
                            })}</td>
                    </tr>
                    {arrayNotEmpty(this.props.statistics.unusedVariables) ?
                        <tr>
                            <td>Unused variables</td>
                            <td>
                                {this.props.statistics.unusedVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment key={index}>
                                        <span className={`badge badge-${this.getVariableColor(variable)}`}>{variable}</span>{' '}
                                    </React.Fragment>)
                                })}</td>
                        </tr>
                        : null}
                    {arrayNotEmpty(this.props.statistics.allDefinedVariables) ?
                        <tr>
                            <td>Defined variables</td>
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
                            <td>Master variables</td>
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
                            <td>Dependent variables</td>
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
                            <td>Undefined variables</td>
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
                            <td>Conditionals</td>
                            <td>
                                {this.props.statistics.conditionals.conditionalsSet.map((conditional, index) => {
                                    // Return the element. Also pass key
                                    return (<li key={index}>{this.renderConditional(conditional)}</li>)
                                })}</td>
                        </tr>
                        : null}
                    </tbody>
                </Table>
            </React.Fragment>
        );
    }
}

export default TemplateStatistics;