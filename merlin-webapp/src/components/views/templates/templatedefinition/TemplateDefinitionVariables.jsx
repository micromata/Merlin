import React from 'react';
import {
    Form,
    Table
} from 'reactstrap';
import EditableTextField from "../../../general/forms/EditableTextField"
import {arrayNotEmpty} from "../../../../utilities/global";
import {FormCheckbox} from "../../../general/forms/FormComponents";

class TemplateDefinitionVariables extends React.Component {

    render = () => {
        if (!this.props.definition.variableDefinitions) {
            return null;
        }
        const rows = [];
        this.props.definition.variableDefinitions.forEach((variable, index) => {
            let range = '';
            if (variable.minimumValue || variable.maximumValue) {
                range = `${variable.minimumValue}-${variable.maximumValue}`;
            } else if (arrayNotEmpty(variable.allowedValuesList)) {
                range = variable.allowedValuesList.join(', ');
            }

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
                        <FormCheckbox checked={variable.required}
                               name={'required'}
                               onChange={(event) => this.props.handleStateChange(event, index)}/>
                    </td>
                    <td style={{textAlign: 'center'}}>
                        <FormCheckbox checked={variable.unique}
                               name={'unique'}
                               onChange={(event) => this.props.handleStateChange(event, index)}/>
                    </td>
                    <td>
                        {range}
                    </td>
                    <td>{variable.description}</td>
                </tr>
            );
        });
        return <React.Fragment>
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
        </React.Fragment>;
    };
}

export default TemplateDefinitionVariables;