import React from 'react';
import './style.css';

class Template extends React.Component {


    render = () => <div
        className={'template-container'}
    >
        <div className={'template'}>
            <span className={'template-name'}>{this.props.name}</span>
            <span className={'template-description'}>{this.props.description}</span>
            <span className={'hint'}>Click to run.</span>
        </div>
    </div>
}

export default Template;