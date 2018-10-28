import React from 'react';
import DirectoryItem from "./DirectoryItem";
import {IconAdd} from '../../general/IconComponents';
import {FormButton} from "../../general/forms/FormComponents";
import I18n from "../../general/translation/I18n";

class DirectoryItemsFieldset extends React.Component {
    constructor(props) {
        super(props);
        this.onAdd = this.onAdd.bind(this);
    }

    onAdd = event => {
        // event.preventDefault();
        this.props.addItem();
    }

    render() {
        var items = this.props.items.map((item, index) => {
            return (
                <DirectoryItem item={item} key={index} index={index} removeItem={this.props.removeItem}
                               onDirectoryChange={this.props.onDirectoryChange}
                               onRecursiveFlagChange={this.props.onRecursiveFlagChange}/>
            );
        });
        return (
            <fieldset className="form-group">
                <legend><I18n name={'configuration.templatesDirectory'}/></legend>
                {items}
                <div className="form-group row">
                    <div className="col-sm-2"></div>
                    <div className="col-sm-10">
                        <FormButton onClick={this.onAdd}
                                hintKey={'configuration.addDirectory.hint'}><IconAdd/> <I18n name={'configuration.addDirectory'}/></FormButton>
                    </div>
                </div>
            </fieldset>
        );
    }
}

export default DirectoryItemsFieldset;

