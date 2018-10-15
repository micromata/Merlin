import React from 'react';
import {Table} from 'reactstrap';
import {notEmpty} from '../../../utilities/global';

class TemplateStatistics extends React.Component {
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
                    {notEmpty(this.props.statistics.unusedVariables) ?
                        <tr>
                            <td>Unused variables</td>
                            <td>
                                {this.props.statistics.unusedVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment
                                        key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                                })}</td>
                        </tr>
                        : undefined}
                    {notEmpty(this.props.statistics.allDefinedVariables) ?
                        <tr>
                            <td>Definied variables</td>
                            <td>
                                {this.props.statistics.allDefinedVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment
                                        key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                                })}</td>
                        </tr>
                        : undefined}
                    {notEmpty(this.props.statistics.undefinedVariables) ?
                        <tr>
                            <td>Undefinied variables</td>
                            <td>
                                {this.props.statistics.undefinedVariables.map((variable, index) => {
                                    // Return the element. Also pass key
                                    return (<React.Fragment
                                        key={index}>{index > 0 && ', '}{`{${variable}}`}</React.Fragment>)
                                })}</td>
                        </tr>
                        : undefined}
                    </tbody>
                </Table>
            </React.Fragment>
        );
    }
}

export default TemplateStatistics;