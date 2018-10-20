import React from 'react';
import {connect} from 'react-redux';
import {loadVersion} from '../../../actions';
import './style.css'
import {formatDateTime} from '../../../utilities/global';

class Footer extends React.Component {
    componentDidMount = () => {
        this.props.loadVersion();
    };

    render = () => {
        console.log(this.props);

        return <div className={'footer'}>
            <p className={'version'}>Version {this.props.version} * Build Date {this.props.buildDate}</p>
        </div>;
    };
}

const mapStateToProps = state => ({
    version: state.version.version,
    buildDate: formatDateTime(state.version.buildDate)
});

const actions = {
    loadVersion
};

export default connect(mapStateToProps, actions)(Footer);