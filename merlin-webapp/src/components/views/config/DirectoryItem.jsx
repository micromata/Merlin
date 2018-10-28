import React from 'react';
import {FormGroup, FormLabel, FormField, FormCheckbox, FormButton} from "../../general/forms/FormComponents";
import EditableTextField from "../../general/forms/EditableTextField";
import {getRestServiceUrl} from "../../../utilities/global";
import {IconRemove} from '../../general/IconComponents';
import I18n from "../../general/translation/I18n";

class DirectoryItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            fileBrowserResult: props.item.directory
        }
        this.handleDirectoryChange = this.handleDirectoryChange.bind(this);
        this.handleRecursiveFlagChange = this.handleRecursiveFlagChange.bind(this);
        this.onClickRemove = this.onClickRemove.bind(this);
    }

    handleDirectoryChange = value => {
        this.props.onDirectoryChange(this.props.index, value);
    }

    handleRecursiveFlagChange = event => {
        this.props.onRecursiveFlagChange(this.props.index, event.target.checked);
    }

    onClickRemove() {
        var index = parseInt(this.props.index, 10);
        this.props.removeItem(index);
    }

    browseDirectory = () => {
        const current = "&current=" + encodeURIComponent(this.props.item.directory);
        fetch(getRestServiceUrl("files/browse-local-filesystem?type=dir" + current), {
            method: "GET",
            dataType: "JSON",
            headers: {
                "Content-Type": "text/plain; charset=utf-8",
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                if (data.directory) {
                    this.setState({fileBrowserResult: data.directory})
                    this.props.onDirectoryChange(this.props.index, data.directory)
                }
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }


    render() {
        return (
            <FormGroup>
                <FormLabel i18nKey={'configuration.templatesDirectory'} />
                <FormField length={6}>
                    <EditableTextField
                        type={'text'}
                        value={this.props.item.directory}
                        name={'directory'}
                        onChange={this.handleDirectoryChange}
                    />
                </FormField>
                <FormField length={2}>
                    <FormCheckbox checked={this.props.item.recursive}
                           name="recursive" labelKey={'configuration.recursive'}
                           onChange={this.handleRecursiveFlagChange}
                           hintKey={'configuration.recursive.hint'} />
                </FormField>
                <FormField length={2}>
                    <FormButton onClick={this.browseDirectory}
                            hintKey={'common.browse.hint'}><I18n name={'common.browse'}/>
                    </FormButton>
                    <FormButton onClick={this.onClickRemove}
                            hintKey={'configuration.removeItem'}><IconRemove />
                    </FormButton>
                </FormField>
            </FormGroup>
        );
    }
}

export default DirectoryItem;

