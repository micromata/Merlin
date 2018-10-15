import React from 'react';
import {Table} from 'reactstrap';

class TemplateStatistics extends React.Component {
    render() {
        return (
            <React.Fragment>
                <h4>Statistics</h4>
                <Table hover>
                    <tbody>
                    <tr>
                        <td>Used variables:</td>
                        <td>
                            {this.props.statistics.usedVariables.map((variable, index) => {
                            // Return the element. Also pass key
                                return (<span key={index}>{`{${variable}}`} </span>)
                        })}</td>
                    </tr>
                    </tbody>
                </Table>
            </React.Fragment>
        );
    }
}

export default TemplateStatistics;