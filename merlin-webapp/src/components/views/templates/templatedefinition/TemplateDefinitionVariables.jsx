import React from 'react';
import {
    Form,
    Table
} from 'reactstrap';
import EditableTextField from "../../../general/forms/EditableTextField"
import {arrayNotEmpty} from "../../../../utilities/global";
import {FormCheckbox} from "../../../general/forms/FormComponents";
import I18n from "../../../general/translation/I18n";

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
                        <th><I18n name={'templates.variable'}/></th>
                        <th><I18n name={'templates.variable.type'}/></th>
                        <th><I18n name={'common.required'}/></th>
                        <th><I18n name={'common.unique'}/></th>
                        <th><I18n name={'templates.variable.range'}/></th>
                        <th><I18n name={'common.description'}/></th>
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