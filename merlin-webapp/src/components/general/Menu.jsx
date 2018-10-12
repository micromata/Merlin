import React from 'react';
import {Link} from 'react-router-dom';
import {Nav, NavLink, Navbar, NavItem} from 'reactstrap';

class Menu extends React.Component {

    render() {

        return (
            <div>Test</div>
/*            <Navbar collapseOnSelect>
                <Navbar.Header>
                    <Navbar.Brand>
                        <Link to={'/'}>
                            Merlin Webapp
                        </Link>
                    </Navbar.Brand>
                    <Navbar.Toggle/>
                </Navbar.Header>
                <Navbar.Collapse>
                    <Nav>
                        {
                            this.props.routes.map((route, index) => (
                                <NavLink
                                    key={index}
                                    href={route[1]}
                                    exact
                                >
                                    <NavItem
                                        eventKey={index + 1}
                                    >
                                        {route[0]}
                                    </NavItem>
                                </NavLink>
                            ))
                        }
                    </Nav>
                </Navbar.Collapse>
            </Navbar>*/
        );
    }
}

export default Menu;
