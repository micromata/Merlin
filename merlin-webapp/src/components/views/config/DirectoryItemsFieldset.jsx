import React from 'react';
import DirectoryItem from "./DirectoryItem";
import {IconAdd} from '../../general/IconComponents';

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
                <legend>Template directories</legend>
                {items}
                <div className="form-group row">
                    <div className="col-sm-2"></div>
                    <div className="col-sm-10">
                        <button type="button" onClick={this.onAdd} className="btn btn-outline-primary"
                                title="Add new Template directory row"><IconAdd/> Add directory</button>
                    </div>
                </div>
            </fieldset>
        );
    }
}

export default DirectoryItemsFieldset;

