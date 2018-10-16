import React from 'react';
import {Form, Table} from "reactstrap";
import EditableTextField from "../../../general/forms/EditableTextField";

class TemplateDefinitionDependentVariables extends React.Component {
    render = () => {
        if (!this.props.definition.dependentVariableDefinitions) {
            return null;
        }
        const rows = [];
        this.props.definition.dependentVariableDefinitions.forEach((variable, index) => {
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
                    <td>{variable.dependsOn.name}</td>
                    <td>{variable.mappingList.join(', ')}</td>
                    <td>
                        <ul>{Object.keys(variable.mapping).map((key, index) => {
                            return (<li key={index}>{`${key} => ${variable.mapping[key]}`}</li>);
                        })}</ul>
                    </td>
                </tr>
            );
        });
        return <React.Fragment>
            <Form>
                <Table striped responsive bordered hover>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Depends on</th>
                        <th>Mapping list</th>
                        <th>Mapping</th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows}
                    </tbody>
                </Table>
            </Form>
        </React.Fragment>;
    };
}

export default TemplateDefinitionDependentVariables;