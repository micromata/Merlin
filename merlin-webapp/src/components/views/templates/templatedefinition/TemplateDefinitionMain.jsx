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

class TemplateDefinitionMain extends React.Component {
    render = () => {
        return <div><Form>
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
                    Description
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
                <FormLabel htmlFor={'filenamePattern'}>
                    File name pattern
                </FormLabel>
                <FormField length={6}>
                    <EditableTextField
                        type={'text'}
                        value={this.props.definition.filenamePattern}
                        name={'filenamePattern'}
                        onChange={this.props.handleTextChange}
                    />
                </FormField>
                <FormField length={2}>
                    <FormCheckbox checked={this.props.definition.stronglyRestrictedFilenames}
                                  name="stronglyRestrictedFilenames" label={'strong file names'}
                                  onChange={this.props.handleStateChange}
                                  hint="Merlin will ensure filenames without unallowed chars. If checked, Merlin will only use ASCII-chars and replace e. g. ä by ae (recommended)."/>
                </FormField>
            </FormGroup>
        </Form>
            <h5>Information</h5>
            <Table hover>
                <tbody>
                <tr>
                    <td>Last modified</td>
                    <td>{formatDateTime(this.props.definition.fileDescriptor.lastModified)}</td>
                </tr>
                <tr>
                    <td>Path</td>
                    <td><LinkFile primaryKey={this.props.definition.fileDescriptor.primaryKey}
                                  filepath={this.props.definition.fileDescriptor.canonicalPath}/></td>
                </tr>
                </tbody>
            </Table>

        </div>;
    };
}

export default TemplateDefinitionMain;