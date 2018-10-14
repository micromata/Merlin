import React from 'react';
import {
    Form,
    Table
} from 'reactstrap';
import EditableTextField from "../../../general/forms/EditableTextField"

class TemplateDefinitionVariables extends React.Component {

    render = () => {
        if (!this.props.definition.variableDefinitions) {
            return null;
        }
        const rows = [];
        this.props.definition.variableDefinitions.forEach((variable, index) => {
            rows.push(
                <tr key={variable.name}>
                    <td>
                        <EditableTextField
                            type={'text'}
                            value={variable.name}
                            name={'name'}
                            onChange={this.props.handleTextChange}
                            index={index}/>
                    </td>
                    <td>{variable.type}</td>
                    <td style={{textAlign: 'center'}}>
                        <input type="checkbox" checked={variable.required}
                               name={'required'}
                               onChange={(event) => this.props.handleStateChange(event, index)}/>
                    </td>
                    <td style={{textAlign: 'center'}}>
                        <input type="checkbox" checked={variable.unique}
                               name={'unique'}
                               onChange={(event) => this.props.handleStateChange(event, index)}/>
                    </td>
                    <td>{variable.minimum}-{variable.maximum}</td>
                    <td>{variable.description}</td>
                </tr>
            );
        });
        return <div>
            <Form>
                <Table striped responsive bordered hover>
                    <thead>
                    <tr>
                        <th>Variable</th>
                        <th>Type</th>
                        <th>Required</th>
                        <th>Unique</th>
                        <th>Range</th>
                        <th>Description</th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows}
                    </tbody>
                </Table>
            </Form>
        </div>;
    };
}

export default TemplateDefinitionVariables;