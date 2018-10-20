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
            {statement}
            {childConditionals}
        </React.Fragment>)
    }

    render() {
        return (
            <React.Fragment>
                <h4>Statistics</h4>
                <Table hover>
                    <tbody>
                    <tr>
                        <td>Used variables</td>
                        <td>
                            {this.props.statistics.usedVariables.map((variable, index) => {
                                // Return the element. Also pass key
                                return (
                                    <React.Fragment key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                            })}</td>
                    </tr>
                    {arrayNotEmpty(this.props.statistics.unusedVariables) ?
                        <tr>
                            <td>Unused variables</td>
                            <td>
                                {this.props.statistics.unusedVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment
                                        key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                                })}</td>
                        </tr>
                        : null}
                    {arrayNotEmpty(this.props.statistics.allDefinedVariables) ?
                        <tr>
                            <td>Definied variables</td>
                            <td>
                                {this.props.statistics.allDefinedVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment
                                        key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                                })}</td>
                        </tr>
                        : null}
                    {arrayNotEmpty(this.props.statistics.masterVariables) ?
                        <tr>
                            <td>Master variables</td>
                            <td>
                                {this.props.statistics.masterVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment
                                        key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                                })}</td>
                        </tr>
                        : null}
                    {arrayNotEmpty(this.props.statistics.undefinedVariables) ?
                        <tr>
                            <td>Undefinied variables</td>
                            <td>
                                {this.props.statistics.undefinedVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment
                                        key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                                })}</td>
                        </tr>
                        : null}
                    {arrayNotEmpty(this.props.statistics.conditionals.conditionalsSet) ?
                        <tr>
                            <td>Conditionals</td>
                            <td>
                                {this.props.statistics.conditionals.conditionalsSet.map((conditional, index) => {
                                    console.log("conditional: " + conditional);
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