import React from 'react';
import {formatDateTime} from "../../../../utilities/global";
import {
    Form,
    Table
} from 'reactstrap';
import {
    FormGroup,
    FormField,
    FormLabel,
    FormCheckbox,
} from "../../../general/forms/FormComponents";
import EditableTextField from "../../../general/forms/EditableTextField";
import LinkFile from "../../../general/LinkFile"
import I18n from "../../../general/translation/I18n";

class TemplateDefinitionMain extends React.Component {
    render = () => {
        return <React.Fragment><Form>
            <FormGroup>
                <FormLabel htmlFor={'id'}>
                    Id
                </FormLabel>
                <FormField length={6}>
                    <EditableTextField
                        type={'text'}
                        value={this.props.definition.id}
                        name={'id'}
                        onChange={this.props.handleTextChange}
                    />
                </FormField>
            </FormGroup>
            <FormGroup>
                <FormLabel htmlFor={'description'}>
                    <I18n name={'common.description'}/>
                </FormLabel>
                <FormField length={6}>
                    <EditableTextField
                        type={'textarea'}
                        value={this.props.definition.description}
                        name={'description'}
                        onChange={this.props.handleTextChange}
                    />
                </FormField>
            </FormGroup>
            <FormGroup>
                <FormLabel htmlFor={'filenamePattern'} i18nKey={'templates.config.filenamePattern'} />
                <FormField length={6}>
                    <EditableTextField
                        type={'text'}
                        value={this.props.definition.filenamePattern}
                        name={'filenamePattern'}
                        onChange={this.props.handleTextChange}
                    />
                </FormField>
                <FormField length={3}>
                    <FormCheckbox checked={this.props.definition.stronglyRestrictedFilenames}
                                  name="stronglyRestrictedFilenames" labelKey={'templates.config.strongFilenames'}
                                  onChange={this.props.handleStateChange}
                                  hintKey={'templates.config.strongFilenames.hint'} />
                </FormField>
            </FormGroup>
        </Form>
            <h5><I18n name={'common.information'}/></h5>
            <Table hover>
                <tbody>
                <tr>
                    <td><I18n name={'common.lastModified'}/></td>
                    <td>{formatDateTime(this.props.definition.fileDescriptor.lastModified)}</td>
                </tr>
                <tr>
                    <td><I18n name={'common.path'}/></td>
                    <td><LinkFile primaryKey={this.props.definition.primaryKey}
                                  filepath={this.props.definition.fileDescriptor.canonicalPath}/></td>
                </tr>
                <tr>
                    <td><I18n name={'templates.primaryKey'}/></td>
                    <td>{this.props.definition.primaryKey}</td>
                </tr>
                </tbody>
            </Table>

        </React.Fragment>;
    };
}

export default TemplateDefinitionMain;
